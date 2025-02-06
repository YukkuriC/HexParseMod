import os, json

os.chdir(os.path.dirname(__file__))

spells = ['compile']

if 'prepare':
    spells = [{"id": 'hexparse:' + x, "required": False} for x in spells]
    tags = ['can_start_enlighten', 'per_world_pattern', 'requires_enlightenment']
    tags = [f'action/{x}.json' for x in tags]
    tags = [*tags, *('hexcasting/' + x for x in tags)]
    tags = ['../common/src/main/resources/data/hexcasting/tags/' + x for x in tags]

if 'gen':
    body = json.dumps({"values": spells}, separators=',:')
    for t in tags:
        os.makedirs(os.path.dirname(t), exist_ok=1)
        with open(t, 'w', encoding='utf-8') as f:
            f.write(body)
