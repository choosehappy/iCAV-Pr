def entry = getProjectEntry()
def fname = entry.getImageName()

//def roi_name = 'C:/research/cav/datacd31/json/cav/' + fname.replaceAll(".png","_roi.json").replaceAll(".svs","_roi.json").replaceAll(".ndpi","_roi.json")
def roi_name = 'C:/research/cav/datacd31/json/cav2/' + fname.replaceAll(".png","_roi.json").replaceAll(".svs","_roi.json").replaceAll(".ndpi","_roi.json")


selectObjectsByClassification("CD31ROI");
exportSelectedObjectsToGeoJson(roi_name, "EXCLUDE_MEASUREMENTS", "FEATURE_COLLECTION")
print 'Exported: ' + roi_name