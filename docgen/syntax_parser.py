import yaml, os
from functools import partial

if 'PATHS':
    os.chdir(os.path.dirname(__file__))
    DEST_MD = "../SYNTAX.md"
    SRC = "syntax.yaml"

MODE = 'OUT_MD'

if MODE == "INPUT":
    with open(DEST_MD, 'r', encoding='utf-8') as f:
        lines = f.read().split('\n')
    pool = {}
    for l in lines:
        l = l.strip()
        if l.startswith("#"):
            title = l.lstrip("#").strip()
            entries = {'headers': [], 'data': []}
            headers = None
            pool[title] = entries
            skip_line = False
            continue
        elif not l:
            continue

        texts = [x.strip() for x in l.strip('|').split('|')]

        if skip_line:
            skip_line = False
            continue
        elif entries['headers']:
            e = {}
            for k, v in zip(entries['headers'], texts):
                e[k] = v
            entries['data'].append(e)
        else:
            entries['headers'] = texts
            skip_line = True
    with open(SRC, 'w', encoding='utf-8') as f:
        yaml.dump(pool, f)

if MODE == 'OUT_MD':
    with open(SRC, 'r', encoding='utf-8') as f:
        pool = yaml.load(f, yaml.Loader)
    with open(DEST_MD, 'w', encoding='utf-8') as f:
        pl = partial(print, file=f)
        pt = partial(pl, sep='|')
        for title, entries in pool.items():
            pl('#', title)
            pl()
            data, headers = entries['data'], entries['headers']
            if not data:
                continue
            pt(*headers)
            pt(*(['---'] * len(headers)))
            for e in data:
                pt(*(e[h] for h in headers))