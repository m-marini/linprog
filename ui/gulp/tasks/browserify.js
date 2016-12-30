'use strict';

var browserify = require('browserify');
var config = require('../config');
var partialify = require('partialify');
var gulp = require('gulp');
var debug = require('gulp-debug');
var rename = require('gulp-rename');
var rev = require('gulp-rev');
var source = require('vinyl-source-stream');
var uglify = require('gulp-uglify');
var buffer = require('vinyl-buffer');

// Vendor
gulp.task('vendor', function() {
  return browserify({debug: true})
    .require('jquery')
    .require('bootstrap')
    .require('block-ui')
    .require('lodash')
    .bundle()
    .pipe(source('vendor.js'))
    .pipe(buffer())
    .pipe(uglify())
    .pipe(gulp.dest(config.dist + '/scripts/'));
});

//Browserify
gulp.task('homeApp', function() {
return browserify({debug: true})
 .add('./app/scripts/home.js')
 .external('jquery')
// .external('bootstrap')
 .external('block-ui')
 .external('lodash')
 .transform(partialify) // Transform to allow requiring of templates
 .bundle()
 .pipe(source('home.js'))
 .pipe(buffer())
 .pipe(uglify())
 .pipe(gulp.dest(config.dist + '/scripts/'));
});

//Browserify
gulp.task('mainApp', function() {
return browserify({debug: true})
 .add('./app/scripts/main.js')
 .external('jquery')
// .external('bootstrap')
 .external('block-ui')
 .external('lodash')
 .transform(partialify) // Transform to allow requiring of templates
 .bundle()
 .pipe(source('main.js'))
 .pipe(buffer())
			// .pipe(uglify())
			.pipe(gulp.dest(config.dist + '/scripts/'));
});
