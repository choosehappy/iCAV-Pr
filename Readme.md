

# iCAV-Pr Project
This project has the procedure, code necessary to extract biomarker data from CD31 and Movat stained whole slide images. The results and findings based on this is published as part of a research. 
**Published in Research Article:**

> [Computational Analysis of Routine Biopsies Improves Diagnosis and Prediction of Cardiac Allograft Vasculopathy](https://www.ahajournals.org/doi/10.1161/CIRCULATIONAHA.121.058459) 
>
>>  [Supplementary Material](https://www.ahajournals.org/action/downloadSupplement?doi=10.1161%2FCIRCULATIONAHA.121.058459&file=10.1161.circulationaha.121.058459_supplemental_materials.pdf)  
>
>   *by Eliot G. Peyster, Andrew Janowczyk, Abigail Swamidoss, Samhith Kethireddy, Michael D. Feldman and Kenneth B. Margulies*
> *Originally published 11 Apr 2022*

**Publisher :** [Journal of American Heart Association (JAHA) - Circulation](https://www.ahajournals.org/journal/circ)


## _CD31 Workflow_

QuPath-0.2.3, an open source software platform for digital pathology and WSI image analysis, was used for data extraction from CD31 slides. HistoQC software was downloaded from github using this repository, [https://github.com/choosehappy/HistoQC](https://github.com/choosehappy/HistoQC).

### Segmentation: Annotations and Nuclei Detection

To initiate feature discrimination, the WSIs stained with CD31 were imported into a new QuPath project. The following steps were completed using Apache Groovy scripts.
 - Preprocessing
 - Annotation
 - Nuclei detection


#### Preprocessing

Preprocessing included setting the image type (Brightfield H-DAB), background, and stain deconconvolution. These were the parameters used:

setColorDeconvolutionStains('{"Name" : "H-DAB CD31",  
"Stain 1" : "Hematoxylin", "Values 1" : "0.59666 0.75249 0.27885 ",  
"Stain 2" : "DAB", "Values 2" : "0.24042 0.48171 0.84271 ",  "Background" : " 235 235 243 "}');

### Annotation
The following pixel classifiers and their corresponding parameters were generated in order to annotate specific regions or features of interest within the annotated ROI. Resolution parameters may differ based on the slides.

 - A Region of Interest (ROI) was annotated using a pixel classifier and classified as “CD31ROI”. The following threshold values were used: Resolution: Low (4.02μm/px), Channel: Average, Prefilter: Gaussian, Smoothing Sigma: 4, Threshold: 231, Minimum object size: 4000μm^2, and Minimum hole size: 4000μm^2. 
	
 - CD31Tissue_classifier_v3: Resolution: Very High (0.50μm/px), Channel: Average, Prefilter: Gaussian, Smoothing Sigma: 4, Threshold: 235, Minimum object size: 10μm^2, and Minimum Hole Size: 10μm^2.
	
 - CD31DAB_classifier_v3: Resolution: Full (0.25μm/px), Channel: DAB, Prefilter: Gaussian, Smoothing Sigma: 1.5, Threshold: 0.2, Minimum object size: 10μm^2, and Minimum Hole Size: 10μm^2
	
 - CD31HEMA_classifier_v3: Resolution: Full(0.25μm/px), Channel: Hematoxylin, Prefilter: Gaussian, Smoothing Sigma: 1.5, Threshold: 0.2, Minimum object size: 10μm^2, and Minimum Hole Size: 10μm^2

### Nuclei Detection

Having classified the annotations, positive cell detection was run with the following parameters on the “CD31 Tissue” annotations.  Supplemental Figure S.1 defines the parameters used for running positive cell detection.
![Fig_s1](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Fig_s1.png)

The annotation and nuclei data was exported as GeoJSON files for each WSI. Each WSI file therefore has a corresponding nuclei.json file and annotation.json file.

### Extracting Features

The corresponding nuclei and annotation GeoJSON files were then opened using Python’s Geojson and Shapely libraries. Areas for tissue, DAB, hematoxylin, and nuclei were calculated using the Shapely library and collected in four respective Pandas dataframes. The four data frames were then saved to tis.csv, dab.csv, hem.csv, and nuc.csv. Each of these files contain the cohort, filename, and area. In dab.csv, the corresponding Shapely polygons and polygon coordinates were also saved. 

### Binning and DAB dilation
Dab.csv was loaded in a pandas dataframe for DAB object area assessment and binning by size. Objects where DAB areas were in the top/bottom 0.1% were dropped to compensate for staining artifacts, off-target staining, and rare, sporadically distributed larger coronary vessels. The full distribution of DAB objects is presented in Supplemental Figure S.2, and summary statistics on the sizes of measured DAB objects is presented in Supplemental Table S.1.

### Defining Boundaries for Various Binning Processes
 1. Quartile bin boundaries were calculated using statistics from the csv dataframe for the 25th, 50th, and 75th percentiles.
 2. Standard deviation bin boundaries were calculated by the addition of a new column to the data frame that holds the z-score for that row. The z-score was calculated using the equation (𝐴𝑟𝑒𝑎 − 𝑀𝑒𝑎𝑛) / 𝑆𝑡𝑎𝑛𝑑𝑎𝑟𝑑 𝐷𝑒𝑣𝑖𝑎𝑡𝑖𝑜𝑛. The boundaries were then defined as a) within ±0.5 standard deviations from the mean b) within ±1.0 standard deviation excluding the area between ±0.5 standard deviations c) within ±1.5 standard deviation excluding the area between ±1.0 standard deviations d) within ±2.0 standard deviation excluding the area between ±1.5 standard deviations e) within ±3.0 standard deviation excluding the area between ±2.0 standard deviations f) outside of ±3.0 standard deviations.
  ![Fig_s2](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Fig_s2.png)
  3. Biological bin boundaries were defined as shown in Supplemental Table S.2 (conversion used: area of 1 𝑝𝑖𝑥𝑒𝑙2 = 0.0633𝜇𝑚2).
***Table S.1:  Biologically inspired boundary definitions.*** 
![Tab_s1](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s1.png)
4. Dab areas in the top 1% were classified as outliers and treated as a separate bin



### DAB Dilation and Binning Process
The DAB dataframe was grouped by cohorts and filename. For each group, its corresponding nuc.json file was opened and its nuclei detections were read using shapely and geojson. Shapely’s unary_union was used for dilating the DAB annotation, STRtree was used for searching nuclei centroids, and Shapely’s Within function was used to identify nuclei within DAB. Nuclei that were within overlapping dilated regions were not double counted. The following process was performed by dilating the DAB annotation based on the bins. Supplemental Table S.3 outlines the bin dilation sizes.

- findNucsInDAB_qrt: identify nuclei that were within quartile boundaries
- findNucsInDAB_std: identify nuclei that were within standard deviation boundaries
- findNucsInDAB_bio: identify nuclei that were within biological boundaries
- findNucsInDAB_excl: identify nuclei that were within the excluded DAB boundaries

***Table S.2: Binning strategies and their corresponding bin dilation sizes.***
![Tab_s2](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s2.png)

The final CD31 results were collected and saved to CSV.

## Movats Workflow
Movat's Pentachrome classification started with extracting 2000 x 2000 tiles from the WSI. 
### Segmentation
Movat’s Pentachrome stained tiles included regions of myocytes, collagen, basement membrane, stroma, and nuclei. Supplemental Table S.3 presents each of these features, their corresponding color, and the threshold range used.
4
Tiled images were opened in the BGR color space using OpenCV and a Gaussian blur with a 5 x 5 kernel was applied to this image. A low pass filter, gaussian kernel, was applied in order to reduce random noise in the tiled images. This Gaussian blurred image was then converted to HSV. Feature detection in the HSV space was chosen because BGR includes luminance which makes feature discrimination difficult. HSV abstracts hue while separating illumination making it better suited for feature detection. Each stained feature correlated with a specific range of colors in HSV space defined in Supplemental Table S.4.
However, certain slides were overstained and this created issues with the segmentation process which would lead to false positives for stromal detections. To address this, overstained images were manually identified and brightened using Python’s OpenCV convertScaleAbs with an alpha (contrast) value of 1.5 and a beta (brightness) value of 0 prior to segmentation.
Binary masks of each color’s minimum and maximum range were created. Before calculating the area of the binary masks, post processing using Skimage’s morphology functions was needed to reduce noise. The following Supplemental Table S.5 describes the various morphological parameters used. A myocardium mask was also generated by filling holes in the myocyte mask and dilating it until large sheets of myocytes were confluent.
 ![Tab_s3](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s3.png)
 

![Tab_s4](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s4.png)

### Feature Quantification
After morphology functions were applied, Python’s NumPY was used to calculate the total area for each binary mask, where each binary mask was represented by a specific feature. The features were summed by WSI file and grouped by cohort. This Movats data was then saved to a CSV file.

### Merging CD31 and Movats Data
CD31 and Movats data was merged using Pandas based on corresponding filenames. From this combined dataset, the final set of n=680 features for morphologic model construction were generated. This involved using the extracted parameters as detailed above to calculate different cell/nuclei, tissue-type, and DAB object ratios to assess normalized densities and areas-covered in different parts of the digital slides. Specific examples of generated features can be found in the multivariate results in Supplemental Tables S.8 and S.9 below. A complete list of features is available upon reasonable request. 
![Tab_s5](https://github.com/abbyswamidoss/iCav-Pr/blob/master/misc/Tab_s5.png)


> ### References:
> 1.	Loupy, A., et al., Identification and Characterization of Trajectories of Cardiac Allograft Vasculopathy After Heart
> Transplantation. Circulation, 2020. 141(24): p. 1954-1967.
> 2.	Janowczyk, A., A. Basavanhally, and A. Madabhushi, Stain Normalization using Sparse AutoEncoders (StaNoSA): Application to digital pathology. Computerized Medical Imaging and Graphics, 2017.57: p. 50-61.
