import fitz
p = r"app/src/main/assets/Matriculas Editable.pdf"
doc = fitz.open(p)
print('pages', len(doc))
for i, page in enumerate(doc):
    pix = page.get_pixmap(matrix=fitz.Matrix(2,2))
    out = f"app/src/main/assets/TOMA_DE_MATRICULAS_page_{i+1}.png"
    pix.save(out)
    print('saved', out)
