# Step by step instructions
The root of the cav project is under 

> C:\research\cav>

Any examples you may see are based on this.

## 1 Movat Workflow
### Preprocessing

#### 10_mvt_extract_tiles_from_wsi_openslide.py
- This code creates 2000 x 2000 pixel tiles from Movat WSI slides that are under cav/cav2/mvt/. 
- Run the following commands from root. Note, file pattern and tile destinations match 
```ruby
py .\src\10_mvt_extract_tiles_from_wsi_openslide.py --bgremoved -l 0 -p 2000 C:/research/cav/cav2/mvt/CAV2-DC-*.ndpi -o .\datamvt\tiles\cav2\DC\
py .\src\10_mvt_extract_tiles_from_wsi_openslide.py --bgremoved -l 0 -p 2000 C:/research/cav/cav2/mvt/CAV2-HC-*.ndpi -o .\datamvt\tiles\cav2\HC\
py .\src\10_mvt_extract_tiles_from_wsi_openslide.py --bgremoved -l 0 -p 2000 C:/research/cav/cav2/mvt/CAV2-DY1-*.ndpi -o .\datamvt\tiles\cav2\DY\
```
- This should produce tiles under .\datamvt\tiles\cav2\DC\, .\datamvt\tiles\cav2\HC\, .\datamvt\tiles\cav2\DY\. 
 
#### QC Overstained Tiles
- Go through tiles under .\datamvt\tiles\cav2\DC\ folder, visually identify and move overstained slides under .\datamvt\tiles\cav2\DC\OS\.
- Do the same visual QC for HC and DY.
The step informs the segmentation step to add extra brightness to overstained tiles. 

#### 11_mvt_segmentation.ipynb
- When executed this code will process all the tiles under .\datamvt\tiles\cav2\DC\, .\datamvt\tiles\cav2\HC\, .\datamvt\tiles\cav2\DY\.  It will create binary masks for each tile representing each different colored stained region and will calculate the area of these masks/regions. 

- This area data will be produced for each tile under .\datamvt\output\cav2.
#### 12_mvt_prepare_stat_data.ipynb
- This code goes through the data under .\datamvt\output\cav2 to create one overall datasheet. This datasheet groups each tile by its cohort and filename and lists its corresponding area values for each colored region. 

- This datasheet is then saved under .\datamvt\stats\cav2 as '12_movat_all.csv'.
## 2 CD31 Workflow
### Preprocessing

#### 21_cd31_gjson2_dab.ipynb
- Once nuclei and DAB regions have been identified by QuPath and have been exported as JSON files, this code reads these JSON files from .\datacd31\json\cav2 to organize this information into datasheets produced under .\datacd31\output\cav2 as '21cd31_dab.csv', '21cd31_hem.csv', '21cd31_tis.csv', and '21cd31_nuc.csv'.
- These datasheets calculate areas based on the coordinates provided by the JSON files.
#### 22_cd31_binning.ipynb
- This code goes through the the files under .\datacd31\json\cav2 and under .\datacd31\output\cav2\21cd31_dab.csv. 
- It uses this data to create a datasheet containing information representing whether a nuclei is within a certain distance away from a DAB region. These distances are determined based on which binning strategy is being used: quartile, standard deviation, and biological.
- If DAB areas are dilated according to their size prior to running this code, this datasheet is saved under .\datacd31\output\cav2\22cd31_dab_nuc_dil.csv
- If DAB areas are not dilated prior to running, this datasheet is saved under .\datacd31\output\cav2\22cd31_dab_nuc_udl.csv
## 3 Combined Workflow

#### 31_cav_merge_cd31_mvt.ipynb
- This code goes through files under .\datamvt\output\cav2,  '12_movat_all.csv', and '22cd31_dab_nuc_udl.csv' or '22cd31_dab_nuc_dil.csv' (depending on whether we are looking at dilated or not dilated data).
- It then creates a combined datasheet grouped by cohort and filename with Movats column data (including areas of masked regions) and CD31 data (including areas of nuclei, DAB regions, and various binning data).
- It also includes additional computed columns where areas of different masked regions within the Movats data are summed.
- This combined datasheet is then saved under .\datacd31\output\cav2\ as either '31cd31_mvt_dilated.csv' or '31cd31_mvt_undilated.csv' depending on whether dilated or not dilated data was used.
- For this project, we used dilated data.
#### 32_cav_add_computed_cols.ipynb
- This code then uses '31cd31_mvt_dilated.csv' and adds additional computed columns based on the features/data already obtained and saves this under '32_final_dilated.csv'.
## 4 LASSO ROC & CAV Density Curves
#### 40_cav_lasso_roc.ipynb
- When executed, this code uses  '32_final_dilated.csv' and an additional clinical datasheet similar to this to perform a LASSO Regression in order to determine which features or columns are most predictive of outcome.
- It then returns an ROC curve for these features.
#### 41_make_CAV_density_figures.py
- When executed, this code loads the 21cd31_dab.csv and generates the CAV density curves
