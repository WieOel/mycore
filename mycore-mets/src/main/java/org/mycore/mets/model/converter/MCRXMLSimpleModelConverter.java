package org.mycore.mets.model.converter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.mycore.common.MCRException;
import org.mycore.mets.model.Mets;
import org.mycore.mets.model.files.FileGrp;
import org.mycore.mets.model.simple.MCRMetsAltoLink;
import org.mycore.mets.model.simple.MCRMetsFile;
import org.mycore.mets.model.simple.MCRMetsFileUse;
import org.mycore.mets.model.simple.MCRMetsLink;
import org.mycore.mets.model.simple.MCRMetsPage;
import org.mycore.mets.model.simple.MCRMetsSection;
import org.mycore.mets.model.simple.MCRMetsSimpleModel;
import org.mycore.mets.model.struct.IStructMap;
import org.mycore.mets.model.struct.LogicalDiv;
import org.mycore.mets.model.struct.LogicalStructMap;
import org.mycore.mets.model.struct.PhysicalStructMap;
import org.mycore.mets.model.struct.PhysicalSubDiv;

/**
 * This Class is used to converts mets.xml to MCRMetsSimpleModel.
 * @author Sebastian Hofmann(mcrshofm)
 */
public class MCRXMLSimpleModelConverter {

    /**
     * Converts a Document to MetsSimpleModel
     *
     * @param metsDocument the Document which should be converted
     * @return the converted MetsSimpleModel
     * @throws Exception if new Mets(metsDocument) throws exception
     */
    public static MCRMetsSimpleModel fromXML(Document metsDocument) throws Exception {
        Mets mets = new Mets(metsDocument);

        MCRMetsSimpleModel msm = new MCRMetsSimpleModel();

        Map<String, MCRMetsPage> idPageMap = new Hashtable<>();
        Map<String, MCRMetsFile> idFileMap = buildidFileMap(mets);
        List<MCRMetsPage> metsPageList = buildPageList(mets, idPageMap, idFileMap);
        msm.getMetsPageList().addAll(metsPageList);

        Map<String, MCRMetsSection> idSectionMap = new Hashtable<>();
        MCRMetsSection rootMetsSection = buidRootSection(mets, idSectionMap, idFileMap);
        msm.setRootSection(rootMetsSection);

        linkPages(mets, idSectionMap, idPageMap, msm);

        return msm;
    }

    private static MCRMetsSection buidRootSection(Mets mets, Map<String, MCRMetsSection> idSectionMap, Map<String, MCRMetsFile> idFileMap) {
        IStructMap structMap = mets.getStructMap(LogicalStructMap.TYPE);
        LogicalStructMap logicalStructMap = (LogicalStructMap) structMap;
        LogicalDiv divContainer = logicalStructMap.getDivContainer();

        return buildSection(divContainer, idSectionMap, null, idFileMap);
    }

    private static MCRMetsSection buildSection(LogicalDiv current, Map<String, MCRMetsSection> idSectionMap, MCRMetsSection parent, Map<String, MCRMetsFile> idFileMap) {
        MCRMetsSection metsSection = new MCRMetsSection();

        metsSection.setLabel(current.getLabel());
        metsSection.setType(current.getType());
        metsSection.setParent(parent);


        current.getFptrList().forEach(fptr -> {
            fptr.getSeqList().forEach(seq -> {
                seq.getAreaList().forEach(area -> {
                    String fileId = area.getFileId();
                    String begin = area.getBegin();
                    String end = area.getEnd();

                    if(!idFileMap.containsKey(fileId)){
                        throw new MCRException("No file with id " + fileId + " found!");
                    }

                    MCRMetsFile file = idFileMap.get(fileId);
                    MCRMetsAltoLink e = new MCRMetsAltoLink(file, begin, end);
                    metsSection.addAltoLink(e);
                });
            });
        });

        if (idSectionMap != null) {
            idSectionMap.put(current.getId(), metsSection);
        }

        List<MCRMetsSection> childSectionList = metsSection.getMetsSectionList();
        current.getChildren()
                .stream()
                .map(section -> MCRXMLSimpleModelConverter.buildSection(section, idSectionMap, metsSection, idFileMap))
                .forEachOrdered(metsSection::addSection);

        return metsSection;
    }

    private static List<MCRMetsPage> buildPageList(Mets mets, Map<String, MCRMetsPage> idPageMap, Map<String, MCRMetsFile> idFileMap) {
        PhysicalStructMap physicalStructMap = (PhysicalStructMap) mets.getStructMap(PhysicalStructMap.TYPE);
        List<PhysicalSubDiv> physicalSubDivs = physicalStructMap.getDivContainer().getChildren();

        List<MCRMetsPage> result = new ArrayList<>();

        physicalSubDivs.stream()
                .map((physicalSubDiv) -> {
                    // Convert PhysicalSubDiv to MetsPage
                    MCRMetsPage metsPage = new MCRMetsPage();
                    metsPage.setOrderLabel(physicalSubDiv.getOrderLabel());
                    metsPage.setContentIds(physicalSubDiv.getContentids());

                    // Add all MetsFile to the MetsPage
                    List<MCRMetsFile> fileList = metsPage.getFileList();
                    physicalSubDiv.getChildren().stream()
                            .map(file -> idFileMap.get(file.getFileId()))
                            .forEachOrdered(fileList::add);

                    // return a entry of physicalSubDiv.id and MetsPage
                    return new AbstractMap.SimpleEntry<String, MCRMetsPage>(physicalSubDiv.getId(), metsPage);
                })
                .forEachOrdered((entry) -> {
                    // Put page to list
                    result.add(entry.getValue());
                    // Put that generated entry to a Hashtable
                    idPageMap.put(entry.getKey(), entry.getValue());
                });

        return result;
    }

    private static void linkPages(Mets mets, Map<String, MCRMetsSection> idSectionMap, Map<String, MCRMetsPage> idPageMap, MCRMetsSimpleModel metsSimpleModel) {
        mets.getStructLink().getSmLinks().stream()
                .filter(smLink -> idSectionMap.containsKey(smLink.getFrom()) && idPageMap.containsKey(smLink.getTo()))
                .map((smLink) -> {
            MCRMetsSection metsSection = idSectionMap.get(smLink.getFrom());
            MCRMetsPage metsPage = idPageMap.get(smLink.getTo());
            return new MCRMetsLink(metsSection, metsPage);
        }).forEach(metsSimpleModel.getSectionPageLinkList()::add);
    }

    private static Map<String, MCRMetsFile> buildidFileMap(Mets mets) {
        Map<String, MCRMetsFile> idMetsFileMap = new Hashtable<>();
        mets.getFileSec().getFileGroups().forEach((fileGroup) -> {
            addFilesFromGroup(idMetsFileMap, fileGroup);
        });
        return idMetsFileMap;
    }

    private static void addFilesFromGroup(Map<String, MCRMetsFile> idPageMap, FileGrp fileGroup) {
        String fileGroupUse = fileGroup.getUse();
        fileGroup.getFileList().forEach(file -> {
            MCRMetsFileUse use = Enum.valueOf(MCRMetsFileUse.class, fileGroupUse);
            idPageMap.put(file.getId(), new MCRMetsFile(file.getFLocat().getHref(), file.getMimeType(), use));
        });
    }


}
