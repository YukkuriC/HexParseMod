import os, sys, glob, shutil

os.chdir(os.path.dirname(__file__))

for path in glob.glob('../f*/build/libs/*.jar'):
    if 'sources' in path or 'dev' in path:
        continue
    print(path)
    shutil.copy(path, '.')
