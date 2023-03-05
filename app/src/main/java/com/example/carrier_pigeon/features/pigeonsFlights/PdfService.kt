package com.example.carrier_pigeon.features.pigeonsFlights

import android.content.Context
import android.os.Environment
import com.example.carrier_pigeon.R
import com.example.carrier_pigeon.app.utils.dateToShortDate
import com.example.carrier_pigeon.features.pigeonsFlights.data.Record
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

class PdfService(private val context: Context) {
    companion object {
        private val TITLE_FONT = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
        private val BODY_FONT = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
        private const val DOCUMENT_MARGIN_LEFT = 24f
        private const val DOCUMENT_MARGIN_RIGHT = 24f
        private const val DOCUMENT_MARGIN_TOP = 32f
        private const val DOCUMENT_MARGIN_BOTTOM = 32f
        private const val FIRST_LINE_INDENT = 25f
        private const val CELL_PADDING = 8f
    }

    private lateinit var pdf: PdfWriter

    private fun createFile(): File {
        // Prepare file
        val title = context.getString(R.string.pdf_title)
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path, title)
        if (!file.exists()) file.createNewFile()
        return file
    }

    private fun createDocument(): Document {
        // Create Document
        val document = Document()
        document.setMargins(
            DOCUMENT_MARGIN_LEFT,
            DOCUMENT_MARGIN_RIGHT,
            DOCUMENT_MARGIN_TOP,
            DOCUMENT_MARGIN_BOTTOM
        )
        document.pageSize = PageSize.A4
        return document
    }

    private fun setupPdfWriter(document: Document, file: File) {
        pdf = PdfWriter.getInstance(document, FileOutputStream(file))
        pdf.setFullCompression()
        // Open the document
        document.open()
    }

    private fun createParagraph(content: String): Paragraph {
        val paragraph = Paragraph(content, BODY_FONT)
        paragraph.firstLineIndent = FIRST_LINE_INDENT
        paragraph.alignment = Element.ALIGN_JUSTIFIED
        return paragraph
    }

    private fun createTable(column: Int, columnWidth: FloatArray): PdfPTable {
        val table = PdfPTable(column)
        table.widthPercentage = 100f
        table.setWidths(columnWidth)
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

    private fun createCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE
        // setup padding
        cell.setPadding(CELL_PADDING)
        cell.isUseAscender = true
        cell.paddingLeft = CELL_PADDING / 2
        cell.paddingRight = CELL_PADDING / 2
        cell.paddingTop = CELL_PADDING
        cell.paddingBottom = CELL_PADDING
        return cell
    }

    fun createUserTable(
        data: List<Record>,
        paragraphList: List<String>,
        onFinish: (file: File) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Define the document
        val file = createFile()
        val document = createDocument()

        // Setup PDF Writer
        setupPdfWriter(document, file)

        // Add Title
        document.add(Paragraph(context.getString(R.string.the_list_of_boarded_pigeons), TITLE_FONT))
        // Add Empty Line as necessary
        document.add(Paragraph(" "))

        // Add paragraph
        paragraphList.forEach { content ->
            val paragraph = createParagraph(content)
            document.add(paragraph)
        }
        document.add(Paragraph(" "))
        // Add Empty Line as necessary

        // Add table title
        document.add(Paragraph(context.getString(R.string.pigeon_data), TITLE_FONT))
        document.add(Paragraph(" "))

        // Define Table
        val pigeonIdWidth = 0.2f
        val ringSeriesWidth = 1f
        val genderWidth = 0.2f
        val colorWidth = 0.5f
        val vaccineWidth = 0.5f
        val columnWidth =
            floatArrayOf(pigeonIdWidth, ringSeriesWidth, genderWidth, colorWidth, vaccineWidth)
        val table = createTable(5, columnWidth)
        // Table header (first row)
        val tableHeaderContent = listOf(
            context.getString(R.string.nr),
            context.getString(R.string.ring_series),
            context.getString(R.string.sex),
            context.getString(R.string.color),
            context.getString(R.string.no_of_vaccines)
        )
        // write table header into table
        tableHeaderContent.forEach {
            // define a cell
            val cell = createCell(it)
            // add our cell into our table
            table.addCell(cell).horizontalAlignment
        }
        // write pigeon data into table
        data.forEach {
            // Write Each Pigeon Nr
            val noCell = createCell((data.indexOf(it) + 1).toString())
            table.addCell(noCell)
            // Write Each Ring Series
            val ringSeriesCell = createCell(
                context.getString(
                    R.string.ring_series_format,
                    it.country,
                    it.dateToShortDate(),
                    it.series
                )
            )
            table.addCell(ringSeriesCell)
            // Write Each Pigeon Gender
            val genderCell = createCell(it.gender)
            table.addCell(genderCell)
            // Write Each Pigeon Color
            val colorCell = createCell(it.color)
            table.addCell(colorCell)
            // Write Each Pigeon Vaccine
            val vaccineCell =
                createCell("${it.firstVaccine + it.secondVaccine + it.thirdVaccine}")
            table.addCell(vaccineCell)
        }
        document.add(table)
        document.close()

        try {
            pdf.close()
        } catch (ex: Exception) {
            onError(ex)
        } finally {
            onFinish(file)
        }
    }
}
