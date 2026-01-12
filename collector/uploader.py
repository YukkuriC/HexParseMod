from YukkuriC.minecraft.uploader import *

load_cfg_changelog('config.json', 'changelog.md')

push_file = build_pusher()
push_all('.', push_file)
