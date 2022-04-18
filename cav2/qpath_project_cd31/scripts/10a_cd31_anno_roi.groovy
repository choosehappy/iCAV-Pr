def entry = getProjectEntry()
def fname = entry.getImageName()

clearAllObjects();

// Analyse Pre-Process Estimate Stain vectors
setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB CD31", "Stain 1" : "Hematoxylin", "Values 1" : "0.59666 0.75249 0.27885 ", "Stain 2" : "DAB", "Values 2" : "0.24042 0.48171 0.84271 ", "Background" : " 235 235 243 "}');//CAV2
//setColorDeconvolutionStains('{"Name" : "H-DAB CD31", "Stain 1" : "Hematoxylin", "Values 1" : "0.65879 0.63795 0.39877 ", "Stain 2" : "DAB", "Values 2" : "0.36222 0.56582 0.74071 ", "Background" : " 242 243 242 "}'); //CAV1

resetSelection();

print 'Running CD31ROI_classifier_v4 ...'
createSelectAllObject(true); // create a full image annotation
// Create a frame to avoid edges bleeding in as CD31Tissue
runPlugin('qupath.lib.plugins.objects.DilateAnnotationPlugin', '{"radiusMicrons": -100.0,  "lineCap": "Flat",  "removeInterior": false,  "constrainToParent": false}');
clearSelectedObjects(true);
//createAnnotationsFromPixelClassifier("CD31ROI_classifier_v3", 10.0, 0.0) //CAV1
createAnnotationsFromPixelClassifier("CD31ROI_classifier_v4", 4000.0, 4000.0)//CAV2

print 'Running Cleanup ...'
// Get all the annotations without a classname and remove
toRemove = getAnnotationObjects().findAll{it.getPathClass()==null}
removeObjects(toRemove, true)

getAnnotationObjects().each {
   it.setLocked(false)
}

print 'ROI done for: ' + fname