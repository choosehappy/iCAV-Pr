## Step by step instructions

## Movat Workflow
#### Preprocessing
#### 10_mvt_extract_tiles_from_wsi_openslide.py
command to run for creating Movat tiles from cav2 WSI slide set. 

repeat the following command by changing the file-patterns & tile-destination to DC, HC and DY1

run this from C:\research\cav>

    py .\src\10_mvt_extract_tiles_from_wsi_openslide.py --bgremoved -l 0 -p 2000 C:/research/cav/cav2/mvt/CAV2-DC-*.ndpi -o .\datamvt\tiles\cav2\DC\
This should produce tiles under tiles\cav2\DC\, tiles\cav2\HC\, tiles\cav2\DY1\, etc. 
 
#### QC Overstained Tiles
Go through tiles folder and visually identify and separate overstained slides. All overstained tiles for DC cohort should be under tiles\cav2\DC\OS\,  HC cohort under tiles\cav2\HC\OS\, and so on.
The step informs the segmentation step to add extra brightness to overstained tiles. 

#### 11_mvt_segmentation.ipynb
When executed this code will process all the tiles from 
#### 12_mvt_prepare_stat_data.ipynb

## CD31 Workflow
#### Preprocessing

#### 21_cd31_gjson2_dab.ipynb

#### 22_cd31_binning.ipynb

#### 31_cav_merge_cd31_mvt.ipynb

#### 32_cav_add_computed_cols.ipynb

#### 40_cav_lasso_roc.ipynb