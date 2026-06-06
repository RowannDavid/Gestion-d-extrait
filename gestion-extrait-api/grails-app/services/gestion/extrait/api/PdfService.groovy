package gestion.extrait.api

import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator  // ✅ Import manquant
import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat

@Transactional
class PdfService {

    String genererExtrait(Demande demande) {

        // Dossier de sauvegarde
        String dossier = "extraits"
        new File(dossier).mkdirs()

        String nomFichier = "extrait_${demande.reference}_${System.currentTimeMillis()}.pdf"
        String cheminFichier = "${dossier}/${nomFichier}"

        Document document = new Document(PageSize.A4)
        PdfWriter.getInstance(document, new FileOutputStream(cheminFichier))
        document.open()

        // ===== POLICES =====
        Font fontTitre     = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD)
        Font fontSousTitre = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD)
        Font fontLabel     = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)
        Font fontValeur    = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL)
        Font fontPetit     = new Font(Font.FontFamily.HELVETICA, 9,  Font.ITALIC)

        // ===== EN-TÊTE =====
        Paragraph entete = new Paragraph()
        entete.alignment = Element.ALIGN_CENTER
        entete.add(new Phrase("REPUBLIQUE DE CÔTE D'IVOIRE\n", fontSousTitre))
        entete.add(new Phrase("Union - Discipline - Travail\n\n", fontPetit))
        entete.add(new Phrase("MAIRIE DE COCODY\n", fontSousTitre))
        entete.add(new Phrase("Service de l'État Civil\n\n", fontValeur))
        document.add(entete)

        // Ligne séparatrice
        LineSeparator separator = new LineSeparator()
        document.add(new Chunk(separator))
        document.add(Chunk.NEWLINE)

        // ===== TITRE =====
        Paragraph titre = new Paragraph("EXTRAIT D'ACTE DE NAISSANCE", fontTitre)
        titre.alignment = Element.ALIGN_CENTER
        titre.spacingAfter = 20
        document.add(titre)

        // ===== RÉFÉRENCE =====
        Paragraph ref = new Paragraph()
        ref.alignment = Element.ALIGN_CENTER
        ref.add(new Phrase("Référence : ", fontLabel))
        ref.add(new Phrase(demande.reference, fontValeur))
        ref.spacingAfter = 20
        document.add(ref)

        // ===== INFOS PERSONNE =====
        document.add(new Paragraph("INFORMATIONS DU DEMANDEUR", fontSousTitre))
        document.add(Chunk.NEWLINE)

        // Tableau des infos
        PdfPTable table = new PdfPTable(2)
        table.widthPercentage = 100
        table.setWidths([1f, 2f] as float[])
        table.spacingAfter = 20

        ajouterLigne(table, "Nom :",           demande.nom,           fontLabel, fontValeur)
        ajouterLigne(table, "Prénoms :",        demande.prenoms,       fontLabel, fontValeur)
        ajouterLigne(table, "Genre :",          demande.genre?.toString(), fontLabel, fontValeur)
        ajouterLigne(table, "Date de naissance :",
                demande.dateNaissance ? new SimpleDateFormat("dd/MM/yyyy").format(demande.dateNaissance) : "",
                fontLabel, fontValeur)
        ajouterLigne(table, "Lieu de naissance :", demande.villeNaissance, fontLabel, fontValeur)
        ajouterLigne(table, "Nom du parent :",  demande.nomParent,     fontLabel, fontValeur)
        document.add(table)

        // ===== INFOS DEMANDE =====
        document.add(new Paragraph("INFORMATIONS DE LA DEMANDE", fontSousTitre))
        document.add(Chunk.NEWLINE)

        PdfPTable table2 = new PdfPTable(2)
        table2.widthPercentage = 100
        table2.setWidths([1f, 2f] as float[])
        table2.spacingAfter = 30

        ajouterLigne(table2, "Type d'extrait :",  demande.typeExtrait?.toString(), fontLabel, fontValeur)
        ajouterLigne(table2, "Lieu de livraison :", demande.lieuLivraison,         fontLabel, fontValeur)
        ajouterLigne(table2, "Statut :",           demande.statut?.toString(),     fontLabel, fontValeur)
        ajouterLigne(table2, "Date de demande :",
                demande.dateCreated ? new SimpleDateFormat("dd/MM/yyyy").format(demande.dateCreated) : "",
                fontLabel, fontValeur)
        document.add(table2)

        // ===== SIGNATURE =====
        document.add(new Chunk(separator))
        document.add(Chunk.NEWLINE)

        Paragraph signature = new Paragraph()
        signature.alignment = Element.ALIGN_RIGHT
        signature.add(new Phrase("Abidjan, le ${new SimpleDateFormat('dd/MM/yyyy').format(new Date())}\n\n", fontValeur))
        signature.add(new Phrase("L'Officier de l'État Civil\n\n\n\n", fontLabel))
        signature.add(new Phrase("_______________________", fontValeur))
        document.add(signature)

        // ===== PIED DE PAGE =====
        Paragraph pied = new Paragraph()
        pied.alignment = Element.ALIGN_CENTER
        pied.spacingBefore = 30
        pied.add(new Phrase("Document généré le ${new SimpleDateFormat('dd/MM/yyyy HH:mm').format(new Date())}\n", fontPetit))
        pied.add(new Phrase("Ce document est officiel et certifié par la Mairie de Cocody", fontPetit))
        document.add(pied)

        document.close()

        // ===== Sauvegarder en base =====
        Extrait extrait = new Extrait(
                nomFichier:    nomFichier,
                cheminFichier: cheminFichier,
                demande:       demande
        )
        extrait.save(flush: true)

        return cheminFichier
    }

    private void ajouterLigne(PdfPTable table, String label, String valeur, Font fontLabel, Font fontValeur) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, fontLabel))
        cellLabel.border = Rectangle.NO_BORDER
        cellLabel.paddingBottom = 8

        PdfPCell cellValeur = new PdfPCell(new Phrase(valeur ?: "", fontValeur))
        cellValeur.border = Rectangle.NO_BORDER
        cellValeur.paddingBottom = 8

        table.addCell(cellLabel)
        table.addCell(cellValeur)
    }
}