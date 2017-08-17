# AST tree diff API

## Build
There are two ways to get jar of application:

1. With all dependencies in box
```
gradle fatJar
```
2. Only with application code
```
gradle jar
```

## Usage
To start API server run
```
java -jar path/to/build/amorph.jar
```

## Endpoints
There is only one API endpoint - for computing diff between two ASTs

### /api/diff
Parameters:

- `src` JSON representing AST of source code
- `dst` JSON representing AST of destination code to convert source into

Response:
JSON array of patches to apply to source tree. There are 4 types of patches that are plain JSON objects with following fields:

#### Delete
- `type` == `"delete"`
- `node` - pk of node to delete from `src`

#### Insert
- `type` == `"insert"`
- `node` - pk of node from `dst` to insert (without children)
- `parent` - pk of node from `src` that will be new parent of `node`
- `pos` - position in `parent` children to insert `node` into

#### Move
- `type` == `"move"`
- `node` - pk of node from `src` to move (with whole subtree under it)
- `parent` - pk of node from `src` that will be new parent of `node`
- `pos` - position in `parent` children to insert `node` into

#### Update
- `type` == `"update"`
- `node` - node from `src` that needs update of properties
- `props` - JSON object of new properties for `node`

**NOTE** Patches after `Insert` can address to inserted nodes as part of `src` tree so you should keep you source tree up-to-date
