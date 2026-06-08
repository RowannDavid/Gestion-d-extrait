package gestion.extrait.api

import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator
import grails.gorm.transactions.Transactional
import java.text.SimpleDateFormat

@Transactional
class PdfService {

    def grailsApplication

    String genererExtrait(Demande demande) {

        String dossierConfig = grailsApplication.config.getProperty('app.extraits.dossier', 'extraits')
        String dossier = System.getProperty("user.dir") + File.separator + dossierConfig
        new File(dossier).mkdirs()

        String nomFichier = "extrait_${demande.reference}_${System.currentTimeMillis()}.pdf"
        String cheminFichier = dossier + File.separator + nomFichier

        Document document = new Document(PageSize.A4, 40, 40, 40, 40)
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(cheminFichier))
        document.open()

        // ===== POLICES =====
        Font fontTitre      = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)
        Font fontSousTitre  = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD)
        Font fontNormal     = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL)
        Font fontBold       = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)
        Font fontPetit      = new Font(Font.FontFamily.HELVETICA, 8,  Font.NORMAL)
        Font fontPetitBold  = new Font(Font.FontFamily.HELVETICA, 8,  Font.BOLD)
        Font fontGrand      = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD)

        // ===== EN-TÊTE - 3 colonnes =====
        PdfPTable headerTable = new PdfPTable(3)
        headerTable.widthPercentage = 100
        headerTable.setWidths([1f, 1.5f, 1f] as float[])
        headerTable.spacingAfter = 5

        // Colonne gauche
        PdfPCell cellGauche = new PdfPCell()
        cellGauche.border = Rectangle.NO_BORDER
        cellGauche.addElement(new Paragraph("REPUBLIQUE DE\nCÔTE D'IVOIRE", fontPetitBold))
        cellGauche.addElement(new Paragraph("Union - Discipline\nTravail", fontPetit))
        headerTable.addCell(cellGauche)

        // Colonne centre
        PdfPCell cellCentre = new PdfPCell()
        cellCentre.border = Rectangle.NO_BORDER
        cellCentre.horizontalAlignment = Element.ALIGN_CENTER
        cellCentre.addElement(new Paragraph("DISTRICT AUTONOME\nD'ABIDJAN", fontPetitBold) {{ alignment = Element.ALIGN_CENTER }})
        cellCentre.addElement(new Paragraph("VILLE D'ABIDJAN", fontPetitBold) {{ alignment = Element.ALIGN_CENTER }})
        cellCentre.addElement(new Paragraph("COMMUNE DE COCODY", fontSousTitre) {{ alignment = Element.ALIGN_CENTER }})
        cellCentre.addElement(new Paragraph("SOUS-PREFECTURE\nDE COCODY", fontPetit) {{ alignment = Element.ALIGN_CENTER }})
        cellCentre.addElement(new Paragraph("ETAT CIVIL", fontSousTitre) {{ alignment = Element.ALIGN_CENTER }})
        headerTable.addCell(cellCentre)

        // Colonne droite
        PdfPCell cellDroite = new PdfPCell()
        cellDroite.border = Rectangle.NO_BORDER
        cellDroite.horizontalAlignment = Element.ALIGN_RIGHT
        cellDroite.addElement(new Paragraph(
                "Abidjan, le ${new SimpleDateFormat('dd/MM/yyyy').format(new Date())}",
                fontPetit) {{ alignment = Element.ALIGN_RIGHT }})
        headerTable.addCell(cellDroite)

        document.add(headerTable)

        // Ligne séparatrice
        LineSeparator sep = new LineSeparator(2f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -2)
        document.add(new Chunk(sep))
        document.add(Chunk.NEWLINE)

        // ===== TITRE PRINCIPAL =====
        Paragraph titrePrincipal = new Paragraph("EXTRAIT", fontGrand)
        titrePrincipal.alignment = Element.ALIGN_CENTER
        titrePrincipal.spacingBefore = 5
        titrePrincipal.spacingAfter = 2
        document.add(titrePrincipal)

        Paragraph sousTitrePrincipal = new Paragraph(
                "De la Registre des Actes de Naissance\nde l'Année : ${new SimpleDateFormat('yyyy').format(demande.dateNaissance ?: new Date())}",
                fontNormal)
        sousTitrePrincipal.alignment = Element.ALIGN_CENTER
        sousTitrePrincipal.spacingAfter = 10
        document.add(sousTitrePrincipal)

        document.add(new Chunk(new LineSeparator(1f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -2)))
        document.add(Chunk.NEWLINE)

        // ===== NUMÉRO ACTE =====
        PdfPTable numTable = new PdfPTable(2)
        numTable.widthPercentage = 100
        numTable.setWidths([1f, 2f] as float[])
        numTable.spacingAfter = 10

        ajouterCellule(numTable, "N° de l'Acte :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(numTable, demande.reference ?: "________", fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)

        ajouterCellule(numTable, "Nombre de :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(numTable, "UN", fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)
        document.add(numTable)

        // ===== SECTION NAISSANCE =====
        Paragraph sectionNaissance = new Paragraph("NAISSANCE", fontSousTitre)
        sectionNaissance.spacingBefore = 5
        sectionNaissance.spacingAfter = 8
        document.add(sectionNaissance)

        // Infos naissance
        PdfPTable naissTable = new PdfPTable(4)
        naissTable.widthPercentage = 100
        naissTable.setWidths([0.8f, 1.5f, 0.8f, 1.5f] as float[])
        naissTable.spacingAfter = 10

        // Ligne 1
        ajouterCellule(naissTable, "Nom :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(naissTable, (demande.nom ?: "").toUpperCase(), fontBold, Element.ALIGN_LEFT, Rectangle.BOTTOM)
        ajouterCellule(naissTable, "Prénom(s) :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(naissTable, demande.prenoms ?: "", fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)

        // Ligne 2
        ajouterCellule(naissTable, "Sexe :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(naissTable, demande.genre?.toString() == 'MASCULIN' ? 'Masculin' : 'Féminin', fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)
        ajouterCellule(naissTable, "Née le :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(naissTable,
                demande.dateNaissance ? new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH).format(demande.dateNaissance) : "",
                fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)

        // Ligne 3
        ajouterCellule(naissTable, "À :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(naissTable, demande.villeNaissance ?: "", fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)
        ajouterCellule(naissTable, "Tél :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(naissTable, demande.telephone ?: "", fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)

        document.add(naissTable)

        // ===== SECTION PARENTS =====
        Paragraph sectionParents = new Paragraph("FILIATION", fontSousTitre)
        sectionParents.spacingBefore = 5
        sectionParents.spacingAfter = 8
        document.add(sectionParents)

        PdfPTable parentsTable = new PdfPTable(4)
        parentsTable.widthPercentage = 100
        parentsTable.setWidths([0.8f, 1.5f, 0.8f, 1.5f] as float[])
        parentsTable.spacingAfter = 15

        ajouterCellule(parentsTable, "Père/Mère :", fontBold, Element.ALIGN_LEFT, Rectangle.NO_BORDER)
        ajouterCellule(parentsTable, demande.nomParent ?: "", fontNormal, Element.ALIGN_LEFT, Rectangle.BOTTOM)
        document.add(parentsTable)

        // ===== MENTIONS =====
        document.add(new Chunk(new LineSeparator(1f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -2)))
        document.add(Chunk.NEWLINE)

        Paragraph mentions = new Paragraph("MENTIONS MARGINALES", fontSousTitre)
        mentions.spacingAfter = 30
        document.add(mentions)

        document.add(new Chunk(new LineSeparator(1f, 100f, BaseColor.GRAY, Element.ALIGN_CENTER, -2)))
        document.add(Chunk.NEWLINE)
        document.add(new Chunk(new LineSeparator(1f, 100f, BaseColor.GRAY, Element.ALIGN_CENTER, -2)))
        document.add(Chunk.NEWLINE)

        // ===== SIGNATURE =====
        PdfPTable signTable = new PdfPTable(2)
        signTable.widthPercentage = 100
        signTable.setWidths([1f, 1f] as float[])
        signTable.spacingBefore = 20

        // Colonne gauche - Demandeur
        PdfPCell cellDemandeur = new PdfPCell()
        cellDemandeur.border = Rectangle.NO_BORDER
        cellDemandeur.addElement(new Paragraph("Le Demandeur", fontBold))
        cellDemandeur.addElement(new Paragraph("\n\n\n", fontNormal))
        cellDemandeur.addElement(new Paragraph("_____________________", fontNormal))
        cellDemandeur.addElement(new Paragraph("${demande.nom} ${demande.prenoms}", fontNormal))
        signTable.addCell(cellDemandeur)

        // Colonne droite - Officier
        PdfPCell cellOfficier = new PdfPCell()
        cellOfficier.border = Rectangle.NO_BORDER
        cellOfficier.horizontalAlignment = Element.ALIGN_RIGHT
        cellOfficier.addElement(new Paragraph(
                "Abidjan, le ${new SimpleDateFormat('dd MMMM yyyy', Locale.FRENCH).format(new Date())}",
                fontNormal) {{ alignment = Element.ALIGN_RIGHT }})
        cellOfficier.addElement(new Paragraph(
                "L'Officier de l'État Civil",
                fontBold) {{ alignment = Element.ALIGN_RIGHT }})
        cellOfficier.addElement(new Paragraph("\n\n\n", fontNormal))
        cellOfficier.addElement(new Paragraph(
                "_____________________",
                fontNormal) {{ alignment = Element.ALIGN_RIGHT }})
        signTable.addCell(cellOfficier)

        document.add(signTable)

        // ===== PIED DE PAGE =====
        document.add(Chunk.NEWLINE)
        document.add(new Chunk(new LineSeparator(1f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -2)))

        Paragraph pied = new Paragraph(
                "Référence : ${demande.reference} | Généré le ${new SimpleDateFormat('dd/MM/yyyy HH:mm').format(new Date())}",
                fontPetit)
        pied.alignment = Element.ALIGN_CENTER
        pied.spacingBefore = 5
        document.add(pied)

        document.close()

        // Sauvegarder en base
        Extrait existant = Extrait.findByDemande(demande)
        if (existant) {
            existant.nomFichier = nomFichier
            existant.cheminFichier = cheminFichier
            existant.save(flush: true)
        } else {
            new Extrait(
                    nomFichier:    nomFichier,
                    cheminFichier: cheminFichier,
                    demande:       demande
            ).save(flush: true)
        }

        return cheminFichier
    }

    private void ajouterCellule(PdfPTable table, String texte, Font font,
                                int alignement, int bordure) {
        PdfPCell cell = new PdfPCell(new Phrase(texte ?: "", font))
        cell.border = bordure
        cell.horizontalAlignment = alignement
        cell.paddingBottom = 5
        cell.paddingTop = 3
        table.addCell(cell)
    }

    private void ajouterLigne(PdfPTable table, String label, String valeur,
                              Font fontLabel, Font fontValeur) {
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