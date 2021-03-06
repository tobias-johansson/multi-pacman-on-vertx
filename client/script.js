$(function () {


    var id = window.sessionStorage.getItem("id");
    if (!id) {
        id = uuidv1();
        window.sessionStorage.setItem('id', id);
    }

    var ebUrl = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port + '/eventbus'
    console.log("ubUrl", ebUrl)

    var tileSize = 40;
    var tileScale = tileSize/32;
    var app = new PIXI.Application(18*tileSize, 13*tileSize, {backgroundColor : 0x1099bb});

    document.body.appendChild(app.view);

    sprites = {};

    PIXI.loader
        .add("wall",   "images/wall.png")
        .add("pacman", "images/pacman.png")
        .add("pacmanyou", "images/pacman-you.png")
        .add("ghost", "images/ghost.png")
        .add("ghostyou", "images/ghost-you.png")
        .add("rip", "images/rip.png")
        .load(function() {
            $.getJSON( "maze.json", function( data ) {
                var blocks = data.wallBlocks
                blocks.forEach ( function (block) {
                    var sprite = new PIXI.Sprite(PIXI.loader.resources.wall.texture);
                    sprite.x = block.x * 18 * tileSize;
                    sprite.y = block.y * 18 * tileSize;
                    sprite.scale.x = tileScale;
                    sprite.scale.y = tileScale;
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
            eb.onopen = function(){
                join();
                setInterval(function(){
                    heartbeat();
                }, 1000);
                eb.registerHandler('client', function (err, data) {
                    var state = JSON.parse(data.body);
                    // console.log("state", state);
                    var active = [];
                    state.playerStates.forEach(function(ps) {
                        active.push(ps.player.id);
                        var sprite = sprites[ps.player.id];
                        var tx;
                        if (ps.player.type === 'GHOST') {
                            if (ps.player.id == id)      tx = PIXI.loader.resources.ghostyou.texture;
                            else                         tx = PIXI.loader.resources.ghost.texture;
                        } else {
                            if (ps.status === 'DEAD')    tx = PIXI.loader.resources.rip.texture;
                            else if (ps.player.id == id) tx = PIXI.loader.resources.pacmanyou.texture;
                            else                         tx = PIXI.loader.resources.pacman.texture;
                        }
                        if (!sprite) {
                            sprite = new PIXI.Sprite(tx);
                            app.stage.addChild(sprite);
                        } else {
                            sprite.setTexture(tx);
                        }

                        sprite.anchor.set(0.5);
                        sprite.x = ps.location.x * 18 * tileSize + tileSize/2;
                        sprite.y = ps.location.y * 18 * tileSize + tileSize/2;
                        sprite.scale.x = tileScale;
                        sprite.scale.y = tileScale;
                        switch(ps.direction) {
                            case 'UP':
                                if (ps.player.type === 'GHOST') break;
                                sprite.rotation = Math.PI * -0.5;
                                sprite.scale.x = tileScale;
                                break;
                            case 'DOWN':
                                if (ps.player.type === 'GHOST') break;
                                sprite.rotation = Math.PI * 0.5;
                                sprite.scale.x = tileScale;
                                break;
                            case 'RIGHT':
                                if (ps.player.type === 'GHOST') break;
                                sprite.rotation = 0;
                                sprite.scale.x = tileScale;
                                break;
                            case 'LEFT':
                                sprite.rotation = 0;
                                sprite.scale.x = -tileScale;
                                break;
                        }
                        sprites[ps.player.id] = sprite;
                    });
                    console.log("active", active);
                    for (var key in sprites) {
                        if (!active.includes(key)) {
                            sprites[key].visible = false;
                        }
                    }
                });
            };

            function move(direction) {
                publish('action', {action: direction});
            }

            function join() {
                publish('action', {action: 'JOIN'});
            }

            function heartbeat() {
                publish('heartbeat', {});
            }

            function publish(address, data) {
                data.userId = id;
                console.log("publish", address, data);
                eb.publish(address, data);
            }
        });
});
