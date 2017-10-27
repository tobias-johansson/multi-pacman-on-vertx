$(function () {


    var id = window.sessionStorage.getItem("id");
    if (!id) {
        id = uuidv1();
        window.sessionStorage.setItem('id', id);
    }

    var ebUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus'
    console.log("ubUrl", ebUrl)

    var app = new PIXI.Application(800, 600, {backgroundColor : 0x1099bb});
    document.body.appendChild(app.view);

    sprites = {};

    PIXI.loader
        .add("wall",   "images/wall.png")
        .add("pacman", "images/pacman2.png")
        .add("ghost1", "images/ghost1.png")
        .load(function() {
            $.getJSON( "maze2.json", function( data ) {
                var blocks = data.wallBlocks
                blocks.forEach ( function (block) {
                    var sprite = new PIXI.Sprite(PIXI.loader.resources.wall.texture);
                    sprite.x = block.x * app.renderer.width;
                    sprite.y = block.y * app.renderer.height;
                    // sprite.anchor.set(0.5);
                    sprite.scale.x = app.renderer.width / 18 / 32;
                    sprite.scale.y = app.renderer.height / 13 / 32;
                    app.stage.addChild(sprite);
                });
            });
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

            var eb = new EventBus(ebUrl);
            eb.onopen = function () {
                join();
                eb.registerHandler('client', function (err, data) {
                    var state = JSON.parse(data.body);
                    //console.log("state", state);
                    state.playerStates.forEach(function(ps) {
                        var sprite = sprites[ps.player.id];
                        if (!sprite) {
                            if (ps.player.type === 'GHOST') {
                                sprite = new PIXI.Sprite(PIXI.loader.resources.ghost1.texture);
                            } else {
                                sprite = new PIXI.Sprite(PIXI.loader.resources.pacman.texture);
                            }
                            app.stage.addChild(sprite);
                        }
                        sprite.anchor.set(0.5);
                        sprite.x = ps.location.x * app.renderer.width  + 32/2;
                        sprite.y = ps.location.y * app.renderer.height + 32/2;
                        switch(ps.direction) {
                            case 'UP':
                                if (ps.player.type === 'GHOST') break;
                                sprite.rotation = Math.PI * -0.5;
                                sprite.scale.x = 1;
                                break;
                            case 'DOWN':
                                if (ps.player.type === 'GHOST') break;
                                sprite.rotation = Math.PI * 0.5;
                                sprite.scale.x = 1;
                                break;
                            case 'RIGHT':
                                if (ps.player.type === 'GHOST') break;
                                sprite.rotation = 0;
                                sprite.scale.x = 1;
                                break;
                            case 'LEFT':
                                sprite.rotation = 0;
                                sprite.scale.x = -1;
                                break;
                        }
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
