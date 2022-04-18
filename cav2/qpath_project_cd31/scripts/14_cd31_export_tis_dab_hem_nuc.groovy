def entry = getProjectEntry()
def fname = entry.getImageName()
def nuc_name = 'C:/research/cav/datacd31/json/cav2/' + fname.replaceAll(".png","_nuc.json").replaceAll(".ndpi","_nuc.json")
def ann_name = 'C:/research/cav/datacd31/json/cav2/' + fname.replaceAll(".png","_ann.json").replaceAll(".ndpi","_ann.json")

// ANNOTATIONS
// --- remove measurements, not needed but makes file smaller
Set annotationMeasurements = []
getAnnotationObjects().each{it.getMeasurementList().getMeasurementNames().each{annotationMeasurements << it}}
println(annotationMeasurements)
annotationMeasurements.each{ removeMeasurements(qupath.lib.objects.PathAnnotationObject, it);}
boolean prettyPrint = false 
def gson = GsonTools.getInstance(prettyPrint)
def annotations = getAnnotationObjects()
File annFile = new File(ann_name)
annFile.withWriter('UTF-8') {
    gson.toJson(annotations,it)
}

// NUCLEI
Set detectionMeasurements = []
getDetectionObjects().each{it.getMeasurementList().getMeasurementNames().each{detectionMeasurements << it}}
println(detectionMeasurements)
detectionMeasurements.each{ removeMeasurements(qupath.lib.objects.PathDetectionObject, it);}
def detections = getDetectionObjects()
File detectionFile = new File(nuc_name)
detectionFile.withWriter('UTF-8') {
    gson.toJson(detections,it)
}