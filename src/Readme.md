## Step by step instructions to execute source code
command to run for creating Movat tiles from cav2 WSI slide set. 

repeat the following command by changing the file-patterns & tile-destination to DC, HC and DY1

run this from C:\research\cav>

    py .\src\10_mvt_extract_tiles_from_wsi_openslide.py --bgremoved -l 0 -p 2000 C:/research/cav/cav2/mvt/CAV2-DC-*.ndpi -o .\datamvt\tiles\cav2\DC\