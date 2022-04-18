clearAllObjects();

def entry = getProjectEntry()
def fname = entry.getImageName()
#def jsondir = 'C:/research/QuPath/CD31SVS/export/json/'
#def json4qp = 'C:/research/QuPath/CD31SVS/export/json4qp/'
def nuc_name = json4qp + fname.replaceAll(".png","_nuc_test1.json").replaceAll(".svs","_nuc_4qp.json")
def ann_name = jsondir + fname.replaceAll(".png","_ann.json").replaceAll(".svs","_ann.json")

def gson = GsonTools.getInstance(true)
def nuc_json = new File(nuc_name).text

// Read the detections
def type = new com.google.gson.reflect.TypeToken<List<qupath.lib.objects.PathObject>>() {}.getType()
def detections = gson.fromJson(nuc_json, type)
addObjects(detections) 

// Read the annotations
def ann_json = new File(ann_name).text
def annotations = gson.fromJson(ann_json, type)
def dabClass = getPathClass('CD31DAB')
def dabannotations = annotations.findAll{it.getPathClass() == dabClass}
addObjects(dabannotations) 
 
