
'use strict';

require('should');
var assert = require('assert');

var app = require('../app/assets/javascripts/uploadFiles.js')

describe('upload file block', () => {

    var fileList = app.uploadFileNameSpace

    it('should add new files', () => {
        //
        // fileList.add("testFile")
        //
        // var result = fileList.get()

        console.log(fileList.initialize);
        assert.equal(fileList.add, "Hello, World")

     //   expect(isUndefined(undefined)).toEqual(true);

    });

    it('should delete file when required', () => {


    });
});