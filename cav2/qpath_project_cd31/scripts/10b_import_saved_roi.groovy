def entry = getProjectEntry()
def fname = entry.getImageName()
//def roi_name = 'C:/research/cav/datacd31/json/cav/' + fname.replaceAll(".png","_roi.json").replaceAll(".svs","_roi.json").replaceAll(".ndpi","_roi.json")
def roi_name = 'C:/research/cav/datacd31/json/cav2/' + fname.replaceAll(".png","_roi.json").replaceAll(".svs","_roi.json").replaceAll(".ndpi","_roi.json")


print 'Removing existing annotations ...'
selectObjectsByClassification("CD31HEMA");
clearSelectedObjects();
selectObjectsByClassification("CD31DAB");
clearSelectedObjects();
selectObjectsByClassification("CD31Tissue");
clearSelectedObjects(false);
selectObjectsByClassification("CD31ROI");
clearSelectedObjects();

importObjectsFromFile(roi_name)
setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB CD31", "Stain 1" : "Hematoxylin", "Values 1" : "0.59666 0.75249 0.27885 ", "Stain 2" : "DAB", "Values 2" : "0.24042 0.48171 0.84271 ", "Background" : " 235 235 243 "}');//CAV2

print 'Imported' + roi_name