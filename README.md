# AST tree diff API
Get set of patches to transform one Python3 code into another

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

## Demo
To test API checkout small demo app at the root of your localhost
![Demo app](http://i.imgur.com/EigeiD3.png)

## Endpoints
There is only one API endpoint - for computing diff between two Python3 codes

### /api/diff
Parameters:

- `src` source code
- `dst` destination code to convert source into

Response:
JSON array of patches to apply to source tree. All positions are referring to `src` code. There are 3 types of patches that are plain JSON objects with following fields:

#### Delete
- `type` == `"delete"`
- `start` - position to start delete from
- `stop` - position after last character to delete

#### Insert
- `type` == `"insert"`
- `text` - code piece to insert
- `pos` - position to insert `text` into

#### Update
Synonym for replace
- `type` == `"update"`
- `start` - position to start replace from
- `stop` - position after last character to replace
- `text` - value to replace text in range with

**NOTE** `pos` of insert patch can refer to position further than string length. In this case append to the end is requested
