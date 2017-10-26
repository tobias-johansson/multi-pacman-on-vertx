$(function () {

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

    sprites = {};

    PIXI.loader
        .add("wall",   "images/wall.png")
        .add("pacman", "images/pacman2.png")
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

            document.addEventListener('keydown', function(key) {
                switch(key.keyCode) {
                    case 37:
                        move('LEFT');
                        break;
                    case 38:
                        move('UP');
                        break;
                    case 39:
                        move('RIGHT');
                        break;
                    case 40:
                        move('DOWN');
                        break;
                }
            });

            eb.onopen = function () {
                join();
                eb.registerHandler('client', function (err, data) {
                    var state = JSON.parse(data.body);
                    console.log("state", state);
                    state.playerStates.forEach(function(ps) {
                        var sprite = sprites[ps.player.id];
                        if (!sprite) {
                            sprite = new PIXI.Sprite(PIXI.loader.resources.pacman.texture);
                            app.stage.addChild(sprite);
                        }
                        sprite.x = ps.location.x * app.renderer.width;
                        sprite.y = ps.location.y * app.renderer.height;
                        sprites[ps.player.id] = sprite;
                    });
                });
            };

            function move(direction) {
                publish('action', {action: direction});
            }

            function join() {
                publish('action', {action: 'JOIN'});
            }

            function publish(address, data) {
                data.userId = id;
                console.log("publish", address, data);
                eb.publish(address, data);
            }
        });
});
