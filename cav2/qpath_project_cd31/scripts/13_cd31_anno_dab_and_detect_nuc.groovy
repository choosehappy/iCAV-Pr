print 'Removing existing annotations ...'
selectObjectsByClassification("CD31HEMA");
clearSelectedObjects();
selectObjectsByClassification("CD31DAB");
clearSelectedObjects();
selectObjectsByClassification("CD31Tissue");
clearSelectedObjects(false);

print 'Running CD31Tissue_classifier_v3 ...'
selectObjectsByClassification("CD31ROI");
createAnnotationsFromPixelClassifier("CD31Tissue_classifier_v3", 10, 0)
//createAnnotationsFromPixelClassifier("CD31Tissue_classifier_v1", 100.0, 0.0, "SPLIT")

// Select CD31Tissue and create DAB
print 'Running CD31DAB_classifier_v3 ...'
//selectObjectsByClassification("CD31Tissue");
createAnnotationsFromPixelClassifier("CD31DAB_classifier_v3", 10.0, 0.0)

// Select CD31Tissue and create HEMA
print 'Running CD31HEMA_classifier_v3 ...'
//selectObjectsByClassification("CD31Tissue");
createAnnotationsFromPixelClassifier("CD31HEMA_classifier_v3", 10.0, 0.0)

// Select CD31ROI and run positive cell detection
print 'Running positive Cell detection ...'
selectObjectsByClassification("CD31Tissue");
runPlugin('qupath.imagej.detect.cells.PositiveCellDetection', '{"detectionImageBrightfield": "Hematoxylin OD",  "requestedPixelSizeMicrons": 0.5,  "backgroundRadiusMicrons": 8.0,  "medianRadiusMicrons": 0.0,  "sigmaMicrons": 1.5,  "minAreaMicrons": 10.0,  "maxAreaMicrons": 400.0,  "threshold": 0.1,  "maxBackground": 2.0,  "watershedPostProcess": true,  "excludeDAB": false,  "cellExpansionMicrons": 0.0,  "includeNuclei": true,  "smoothBoundaries": true,  "makeMeasurements": true,  "thresholdCompartment": "Nucleus: DAB OD mean",  "thresholdPositive1": 0.2,  "thresholdPositive2": 0.4,  "thresholdPositive3": 0.6000000000000001,  "singleThreshold": true}');

print 'Running Cleanup ...'
// Get all the annotations without a classname and remove
toRemove = getAnnotationObjects().findAll{it.getPathClass()==null}
removeObjects(toRemove, true)


print 'ALL DONE!'