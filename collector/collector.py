from YukkuriC.minecraft.collector import *

do_collect_arch(__file__)

with open('CHANGELOG.md', encoding='utf-8') as f:
    print(f.read())
