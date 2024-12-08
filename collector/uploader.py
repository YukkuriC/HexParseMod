import os, requests, json
from functools import partial

opentext = partial(open, encoding='utf-8')

with opentext('config.json') as f:
    CFG = json.load(f)

with opentext('changelog.md') as f:
    CHANGELOG = f.read()


def push_file(file):
    filename = os.path.basename(file)
    print("UPLOADING:", filename)
    filename_body = os.path.splitext(filename)[0]
    [_, platform, game_version, mod_version] = filename_body.split('-')
    mod_version_full = f"{game_version}-{mod_version}"

    if "modrinth":
        header = {
            "Authorization": CFG['auth_mr'],
            "User-Agent": "YukkuriC/hexParse",
            # "Content-Type": "multipart/form-data"
        }
        data = {
            "name": filename_body,
            "version_number": mod_version_full,
            "changelog": CHANGELOG,
            "dependencies": [{
                "project_id": "nTW3yKrm",  # hex casting
                "dependency_type": "required"
            }],
            "game_versions": [game_version],
            "version_type": "release",
            "loaders": [platform],
            "featured": True,
            "status": "listed",
            "project_id": "WjFyIzFj",  # hexparse
            "file_parts": [filename],
            "primary_file": filename
        }

        # https://docs.modrinth.com/api/operations/createversion/
        response = requests.post("https://api.modrinth.com/v2/version",
                                 data={
                                     "data": json.dumps(data),
                                 },
                                 headers=header,
                                 files={filename: open(file, 'rb')})
        print("MR", response.text)

    if "curseforge":
        data = {
            "changelog": CHANGELOG,
            "changelogType": "markdown",
            "displayName": filename,
            "gameVersions": [
                # client & server
                *(9638, 9639),
                8326,  # java 17
                7498 if platform == 'forge' else 7499,  # forge or fabric
                9366 if game_version == '1.19.2' else 9990  # 1.19.2 or 1.20.1
            ],
            "releaseType": "release",
        }
        header = {
            "X-Api-Token": CFG['auth_cf'],
        }

        # https://support.curseforge.com/en/support/solutions/articles/9000197321-curseforge-upload-api
        response = requests.post("https://minecraft.curseforge.com/api/projects/1148731/upload-file",
                                 data={
                                     "metadata": json.dumps(data),
                                 },
                                 headers=header,
                                 files={"file": open(file, 'rb')})
        print("CF", response.text)


def pick_versions():
    header = {
        "X-Api-Token": CFG['auth_cf'],
    }
    versions = requests.get("https://minecraft.curseforge.com/api/game/versions", headers=header)
    with opentext('output.json', 'w') as f:
        print(versions.text, file=f)

    exit()


if __name__ == '__main__':
    # pick_versions()
    for sub in os.listdir('.'):
        if not sub.endswith('.jar'):
            continue
        push_file(sub)