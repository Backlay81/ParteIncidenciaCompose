from PyPDF2 import PdfReader
p = r"app/src/main/assets/Matriculas Editable.pdf"
reader = PdfReader(p)
print('PAGES:', len(reader.pages))
try:
    fields = reader.get_fields()
except Exception as e:
    fields = None
print('FIELDS (get_fields):')
if fields:
    for name, info in fields.items():
        print('\n--- FIELD:', name)
        for k, v in info.items():
            print(f"  {k}: {v}")
else:
    print('  None from get_fields()')
    # Try to inspect AcroForm directly
    try:
        root = reader.trailer.get('/Root')
        acro = root.get('/AcroForm') if root else None
        if acro:
            print('\nAcroForm entries:')
            for k, v in acro.items():
                print(f'  {k}: {v}')
        else:
            print('  No AcroForm in trailer')
    except Exception as e:
        print('  Error inspecting AcroForm:', e)
