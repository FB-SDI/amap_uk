module.exports = function(grunt) {

    grunt.initConfig({
        less: {
            development: {
                options: {
                    paths: ["app/assets/css/less"]
                },
                files: {
                    "app/assets/css/css/result.css": "app/assets/css/less/**/*.less"
                }
            }
        },
        jshint: {
            options: {
                globals: {
                    jQuery: true
                },
            },
            uses_defaults: ['app/assets/js/**/*.js'],
            with_overrides: {
                options: {
                    curly: false,
                    undef: true,
                },
                files: {
                    src: ['app/assets/js/*.js', 'app/assets/js/**/*.js', 'app/assets/js/**/**/*.js']
                },
            }
        },
        concat: {
            options: {
                separator: '/* Attaching new lib file */',
            },
            target: {
                files: {
                    'app/assets/build/libraries.js': ['app/assets/lib/*.js', '!app/assets/lib/angular.js', '!app/assets/lib/jquery.min.js', ],
                    'app/assets/build/build.js': ['app/assets/js/*.js', 'app/assets/js/**/*.js', 'app/assets/js/**/**/*.js'],
                    'app/assets/build/build.css': ['app/assets/css/css/result.css']
                }
            }

        },
        uglify: {
            options: {
                mangle: {
                    except: ['jQuery', 'angular', 'app']
                }
            },
            js: {
                files: {
                    'app/assets/build/build.min.js': ['app/assets/build/build.js'],
                    'app/assets/build/libraries.min.js': ['app/assets/build/libraries.js']
                }
            }
        },
        cssmin: {
            options: {
                shorthandCompacting: false,
                roundingPrecision: -1,
                processImport: false
            },
            target: {
                files: {
                    'app/assets/build/build.min.css': ['app/assets/build/build.css']
                }
            }
        },
        express: {
            dev: {
                options: {
                    script: 'app.js'
                }
            }
        },
        watch: {
            js: {
                files: ['app/assets/js/*.js', 'app/assets/js/**/*.js', 'app/assets/js/**/**/*.js', 'app/assets/lib/*.js'],
                tasks: ['concat', 'jshint'],
            },
            scss: {
                files: ['app/assets/css/less/**/*.less'],
                tasks: ['less'],
            },
            css: {
                files: ['app/assets/css/css/*.css'],
                tasks: ['concat'],
            },
            uglify: {
                files: ['app/assets/build/build.js', 'app/assets/build/libraries.js'],
                tasks: ['uglify'],
            },
            cssmin: {
                files: ['app/assets/build/build.css'],
                tasks: ['cssmin'],
            }
        }
    });


    //grunt.registerTask('serve', ['less', 'concat', 'express', 'watch']);

    grunt.registerTask('serve', ['less', 'concat', 'uglify', 'cssmin', 'express', 'watch']);

    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.loadNpmTasks('grunt-express-server');

};