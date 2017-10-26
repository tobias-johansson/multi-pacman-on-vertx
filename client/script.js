$(function () {

//    var id = Math.round($.now() * Math.random());
    var id = uuidv1();

    var ebUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus'
    console.log("ubUrl", ebUrl)
    var eb = new EventBus(ebUrl);

    var app = new PIXI.Application(800, 600, {backgroundColor : 0x1099bb});
    document.body.appendChild(app.view);

    var walls = [
        [1, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 1, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 1, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 1, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 1, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
    ]

    PIXI.loader
        .add("wall", "images/wall.png")
        .add("pacman", "images/pacman.png")
        .load(function() {

            for (i = 0; i < walls.length; i++) {
                for (j = 0; j < walls[i].length; j++) {
                    if (walls[i][j] === 1) {
                        var wall = new PIXI.Sprite(PIXI.loader.resources.wall.texture);
                        wall.x = i * 32;
                        wall.y = j * 32;
                        app.stage.addChild(wall);
                    }
                }
            }

            var pac = new PIXI.Sprite(PIXI.loader.resources.pacman.texture);
            pac.x = app.renderer.width / 2;
            pac.y = app.renderer.height / 2;
            app.stage.addChild(pac);

            document.addEventListener('keydown', function(key) {
                switch(key.keyCode) {
                    case 37:
                        move('left');
                        break;
                    case 38:
                        move('up');
                        break;
                    case 39:
                        move('right');
                        break;
                    case 40:
                        move('down');
                        break;
                }
            });

            eb.onopen = function () {
                join();
                eb.registerHandler('move', function (err, data) {
                    console.log("got", data)
                    switch(data.body.direction) {
                        case 'left':
                            pac.x -= 10; pac.y +=  0;
                            break;
                        case 'up':
                            pac.x +=  0; pac.y -= 10;
                            break;
                        case 'right':
                            pac.x += 10; pac.y +=  0;
                            break;
                        case 'down':
                            pac.x +=  0; pac.y += 10;
                            break;
                        }
                });
            };

            function move(direction) {
                publish('move', {'id': id, 'direction': direction});
            }

            function join() {
                publish('join', {'id': id});
            }

            function publish(address, data) {
                console.log("publish", address, data)
                eb.publish(address, data);
            }


        });
});
