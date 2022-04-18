def entry = getProjectEntry()
def fname = entry.getImageName()
def thumb_name = 'C:/research/cav/datamvt/thumb/cav2/' + fname.replaceAll(".png","_thumb.png").replaceAll(".ndpi","_thumb.png")

def server = getCurrentServer()

// Write the full image downsampled by a factor of 20
def requestFull = RegionRequest.createInstance(server, 20)
writeImageRegion(server, requestFull, thumb_name)