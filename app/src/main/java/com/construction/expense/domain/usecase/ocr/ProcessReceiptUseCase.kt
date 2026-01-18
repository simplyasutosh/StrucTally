package com.construction.expense.domain.usecase.ocr

import android.content.Context
import android.net.Uri
import com.construction.expense.domain.model.ReceiptData
import com.construction.expense.domain.model.LineItem
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for processing receipt images with OCR.
 *
 * Uses Google ML Kit Text Recognition to extract:
 * - Total amount
 * - Date
 * - Vendor name
 * - Invoice number
 * - GST number
 * - Line items
 *
 * Handles Indian receipt formats:
 * - Currency: ₹, Rs, Rs., INR
 * - Dates: DD/MM/YYYY, DD-MM-YYYY, DD.MM.YYYY
 * - GST: 15-character format
 */
@Singleton
class ProcessReceiptUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Process receipt image and extract data
     *
     * @param imageUri URI of receipt image
     * @return Result with ReceiptData on success
     */
    suspend operator fun invoke(imageUri: Uri): Result<ReceiptData> {
        return try {
            // Create InputImage from URI
            val image = InputImage.fromFilePath(context, imageUri)

            // Process image with ML Kit
            val visionText = suspendCancellableCoroutine<com.google.mlkit.vision.text.Text> { continuation ->
                recognizer.process(image)
                    .addOnSuccessListener { result ->
                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            Timber.d("OCR: Extracted ${visionText.textBlocks.size} text blocks")

            // Extract all text
            val allText = visionText.text
            val lines = allText.split("\n")

            // Parse receipt data
            val receiptData = ReceiptData(
                rawText = allText,
                amount = extractAmount(lines, allText),
                date = extractDate(lines, allText),
                vendorName = extractVendorName(lines),
                invoiceNumber = extractInvoiceNumber(lines, allText),
                items = extractLineItems(lines),
                gstNumber = extractGSTNumber(lines, allText),
                gstAmount = extractGSTAmount(lines, allText),
                confidence = calculateConfidence(lines, allText)
            )

            Timber.d("OCR Result: $receiptData")

            Result.success(receiptData)
        } catch (e: Exception) {
            Timber.e(e, "OCR processing failed")
            Result.failure(e)
        }
    }

    /**
     * Extract amount from receipt
     *
     * Looks for patterns:
     * - Total: ₹1,234.56
     * - TOTAL Rs.1234
     * - Grand Total INR 1234.50
     * - Amount: 1234
     */
    private fun extractAmount(lines: List<String>, allText: String): Double? {
        // Regex patterns for Indian currency amounts
        val patterns = listOf(
            // ₹1,234.56 or ₹1234.56 or ₹1234
            Regex("""₹\s*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)"""),
            // Rs.1,234.56 or Rs 1234
            Regex("""Rs\.?\s*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)"""),
            // INR 1234.56
            Regex("""INR\s*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)"""),
            // Just numbers after "Total" or "Amount"
            Regex("""(?:Total|Amount|Grand Total|Net Amount)[\s:]*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)""", RegexOption.IGNORE_CASE)
        )

        val amounts = mutableListOf<Double>()

        // Try each pattern
        for (pattern in patterns) {
            pattern.findAll(allText).forEach { match ->
                val amountStr = match.groupValues[1].replace(",", "")
                try {
                    val amount = amountStr.toDouble()
                    if (amount > 0 && amount < 100_000_000) { // Sanity check: < 10 crore
                        amounts.add(amount)
                    }
                } catch (e: NumberFormatException) {
                    // Ignore invalid numbers
                }
            }
        }

        // Return the largest amount found (usually the total)
        return amounts.maxOrNull()
    }

    /**
     * Extract date from receipt
     *
     * Supports formats:
     * - DD/MM/YYYY
     * - DD-MM-YYYY
     * - DD.MM.YYYY
     * - DD/MM/YY
     */
    private fun extractDate(lines: List<String>, allText: String): Long? {
        val datePatterns = listOf(
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd.MM.yyyy",
            "dd/MM/yy",
            "dd-MM-yy"
        )

        // Regex for date patterns
        val dateRegex = Regex("""\b(\d{1,2})[\/\-\.](\d{1,2})[\/\-\.](\d{2,4})\b""")

        dateRegex.findAll(allText).forEach { match ->
            val dateStr = match.value

            // Try parsing with each format
            for (pattern in datePatterns) {
                try {
                    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
                    formatter.isLenient = false
                    val date = formatter.parse(dateStr)

                    // Validate date is not in future and not too old (10 years)
                    val now = System.currentTimeMillis()
                    val tenYearsAgo = now - (10L * 365 * 24 * 60 * 60 * 1000)

                    if (date != null && date.time in tenYearsAgo..now) {
                        return date.time
                    }
                } catch (e: Exception) {
                    // Try next format
                }
            }
        }

        return null
    }

    /**
     * Extract vendor name
     *
     * Usually in the first 2-3 lines of the receipt
     */
    private fun extractVendorName(lines: List<String>): String? {
        if (lines.isEmpty()) return null

        // Take first non-empty line as vendor name
        val vendorLine = lines.take(5)
            .firstOrNull { line ->
                line.trim().length > 3 &&
                !line.contains(Regex("""\d{10,}""")) && // Not a long number (phone/GST)
                !line.contains(Regex("""[A-Z]{2}\d{2}[A-Z]{5}\d{4}""")) // Not GST number
            }

        return vendorLine?.trim()?.take(100) // Max 100 chars
    }

    /**
     * Extract invoice/bill number
     *
     * Looks for patterns like:
     * - Invoice No: 12345
     * - Bill #12345
     * - Receipt No. 12345
     */
    private fun extractInvoiceNumber(lines: List<String>, allText: String): String? {
        val invoicePattern = Regex(
            """(?:Invoice|Bill|Receipt|Voucher)[\s#No.:-]*([A-Z0-9\-/]+)""",
            RegexOption.IGNORE_CASE
        )

        return invoicePattern.find(allText)?.groupValues?.get(1)?.trim()
    }

    /**
     * Extract GST number
     *
     * Format: 22AAAAA0000A1Z5 (15 characters)
     */
    private fun extractGSTNumber(lines: List<String>, allText: String): String? {
        val gstPattern = Regex("""([0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1})""")

        return gstPattern.find(allText)?.value
    }

    /**
     * Extract GST amount
     *
     * Looks for:
     * - GST: ₹123.45
     * - CGST + SGST amounts
     */
    private fun extractGSTAmount(lines: List<String>, allText: String): Double? {
        val gstPattern = Regex(
            """(?:GST|CGST|SGST|Tax)[\s:]*₹?\s*(\d{1,3}(?:,\d{3})*(?:\.\d{2})?)""",
            RegexOption.IGNORE_CASE
        )

        val amounts = mutableListOf<Double>()

        gstPattern.findAll(allText).forEach { match ->
            val amountStr = match.groupValues[1].replace(",", "")
            try {
                amounts.add(amountStr.toDouble())
            } catch (e: NumberFormatException) {
                // Ignore
            }
        }

        // Return sum of all GST amounts found
        return if (amounts.isNotEmpty()) amounts.sum() else null
    }

    /**
     * Extract line items from receipt
     *
     * Attempts to parse item descriptions with quantities and prices
     */
    private fun extractLineItems(lines: List<String>): List<LineItem>? {
        val items = mutableListOf<LineItem>()

        // Pattern: Item description followed by price
        val itemPattern = Regex("""^(.+?)\s+(\d+)\s*[xX*]\s*(\d+(?:\.\d{2})?)$""")

        for (line in lines) {
            val match = itemPattern.find(line.trim())
            if (match != null) {
                try {
                    val description = match.groupValues[1].trim()
                    val quantity = match.groupValues[2].toIntOrNull()
                    val price = match.groupValues[3].toDoubleOrNull()

                    if (quantity != null && price != null) {
                        items.add(LineItem(description, quantity, price))
                    }
                } catch (e: Exception) {
                    // Skip invalid items
                }
            }
        }

        return if (items.isNotEmpty()) items else null
    }

    /**
     * Calculate confidence score for OCR results
     *
     * Based on:
     * - How many fields were successfully extracted
     * - Text quality
     */
    private fun calculateConfidence(lines: List<String>, allText: String): Float {
        var score = 0f

        // Amount found: +0.4
        if (extractAmount(lines, allText) != null) score += 0.4f

        // Date found: +0.3
        if (extractDate(lines, allText) != null) score += 0.3f

        // Vendor name found: +0.2
        if (extractVendorName(lines) != null) score += 0.2f

        // Invoice number found: +0.05
        if (extractInvoiceNumber(lines, allText) != null) score += 0.05f

        // GST number found: +0.05
        if (extractGSTNumber(lines, allText) != null) score += 0.05f

        return score.coerceIn(0f, 1f)
    }
}
