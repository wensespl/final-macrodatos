from pdf2image import convert_from_path

pdfs = r"C:\Users\USUARIO\Documents\Github\final-macrodatos\ATRE_20324737171_20240520_8032_00.pdf"
pages = convert_from_path(pdfs, 350)

i = 1
for page in pages:
    image_name = "Page_" + str(i) + ".jpg"  
    page.save(image_name, "JPEG")
    i = i+1   