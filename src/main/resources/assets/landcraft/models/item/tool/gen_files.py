#!/usr/local/bin/python3

metals = ['kelline', 'garfax', 'morganine', 'racheline', 'friscion']
tools = ['sword', 'pickaxe', 'axe', 'shovel', 'hoe']
for metal in metals:
    for tool in tools:
        fname = metal+'_'+tool
        file = open(fname+'.json', 'w');
        file.write('''{
    "parent": "item/handheld",
    "textures": {
        "layer0": "landcraft:items/tools/%s"
    }
}''' % (tool+'/'+metal))
        file.close()