var src;
var dst;

function textAreaEditor(id) {
  var element = document.getElementById(id);
  var editor = CodeMirror.fromTextArea(element, {
      mode: {name: "python",
             version: 3,
             singleLineStringErrors: false},
      lineNumbers: true,
      //lineWrapping: true,
      indentUnit: 4
  });
  editor.on('change', function(editor) {
    localStorage.setItem(id, editor.getValue());
  });

  var oldValue = localStorage.getItem(id);
  if (oldValue != null)
    editor.setValue(oldValue)

  return editor;
}

function coordinatesFromPosition(editor, pos) {
  var lines = editor.getValue().match(/[^\n]+\n?|\n/g);

  var lineno = 0;
  var char = pos;
  for (var i = 0; i < lines.length; i++) {
    line = lines[i];
    
    if (char < line.length)
      break;

    char -= line.length;
    lineno++;
  }

  return {line: lineno, ch: char};
}

function addMarker(editor, from, to, className, title='') {
  return editor.markText(
    coordinatesFromPosition(editor, from),
    coordinatesFromPosition(editor, to),
    {
      className: className,
      title: title
    }
  );
}

function displayMarkers(patches) {
  var list = $('#list');
  list.html('');

  for (var i = 0; i < patches.length; i++) {
    patch = patches[i];

    var type = patch.type, start, stop, message;

    if (type == 'insert') {
      start = patch.pos;
      stop = start + 1;
      if (start >= src.getValue().length)
        message = 'Append <code>' + patch.text + '</code> to the end';
      else
        message = 'Insert <code>' + patch.text + '</code> before this character';
    } else {
      start = patch.start;
      stop = patch.stop;

      if (type == 'update')
        message = 'Update this value with <code>' + patch.value + '</code>';
      else
        message = 'Delete this fragment';
    }

    (function(type, start, stop, message) {
      var currentMarker;
      var item = $('<li />');
      item.html(message);
      item.hover(function() {
        currentMarker = addMarker(src, start, stop, type);
        $(this).addClass('mark');
      }, function() {
        currentMarker.clear();
        $(this).removeClass('mark');
      });

      list.append(item);
    })(type, start, stop, message);
  }
}

function getPatchesAndMark() {
  ajax().post('http://127.0.0.1:4567/api/diff', {
    src: src.getValue(),
    dst: dst.getValue()
  }).then(displayMarkers);
}

$(function() {
  src = textAreaEditor("src");
  dst = textAreaEditor("dst");

  $('#send').click(getPatchesAndMark);
})
