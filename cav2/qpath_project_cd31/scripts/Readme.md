Once all the CD31 images (116 of them from /cav2/cd31/*.ndpi) are imported into Qupath,execute the scripts in the following order:

10a_cd31_anno_roi.groovy - annotates roi based on the thresholds

Manually adjust rois if needed to clean up any additional noise. 

11_cd31_export_and_save_roi.groovy - exports roi into a json file under \datacd31\json\cav2

13_cd31_anno_dab_and_detect_nuc.groovy - annotates dabs and detects nuclei within roi

14_cd31_export_tis_dab_hem_nuc.groovy - exports roi into a json files under \datacd31\json\cav2

15_cd31_export_thumb.groovy - exports thumbnail files under \datacd31\thumb\cav2


Additional optional scripts:
10b_import_saved_roi.groovy - after manual annotation if for some reason if we need to reimport use this script.

21_cd31_import_binned_dabs.groovy - after binning if we import this it will show us the binnings for valiation