// scripts/multi-liquid-router.js
const MultiLiquidRouter = extend(LiquidBlock, "multi-liquid-router", {
    liquidPadding: 0,

    icons(){
        return [this.bottomRegion, this.region];
    },

    setBars(){
        this.super$setBars();
        this.removeBar("liquid");
        Vars.content.liquids().each(liquid => {
            this.addLiquidBar(liquid);
        });
    }
});

MultiLiquidRouter.solid = true;
MultiLiquidRouter.noUpdateDisabled = true;
MultiLiquidRouter.canOverdrive = false;
MultiLiquidRouter.floating = true;
MultiLiquidRouter.configurable = true;
MultiLiquidRouter.saveConfig = true;
MultiLiquidRouter.clearOnDoubleTap = true;

MultiLiquidRouter.config(Liquid, (tile, item) => tile.sortLiquid = item);
MultiLiquidRouter.configClear(tile => tile.sortLiquid = null);

MultiLiquidRouter.buildType = prov(() => {
    return extend(LiquidBlock.LiquidBuild, MultiLiquidRouter, {
        sortLiquid: null,

        updateTile(){
            this.super$updateTile();
            if(this.sortLiquid != null){
                this.dumpLiquid(this.sortLiquid);
            }
        },

        draw(){
            // 修正这里：用 this.block 而不是 Vars.content.getContent
            Draw.rect(this.block.bottomRegion, this.x, this.y);
            Draw.rect(this.block.region, this.x, this.y);
        },


        acceptLiquid(source, liquid){
            return true;
        },

        buildConfiguration(table){
            ItemSelection.buildTable(
                MultiLiquidRouter,
                table,
                Vars.content.liquids(),
                () => this.sortLiquid,
                item => this.configure(item),
                4,
                4
            );
        }
    });
});