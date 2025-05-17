package io.github.eco_warrior.sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.TextureArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.eco_warrior.entity.gameSprite;
import io.github.eco_warrior.enums.ToolType;

enum CrackState {
    ACTIVE,           // Currently leaking/damaged
    BEING_FIXED,      // In the process of being fixed
    FIXED             // Fully repaired
}


public class PipeCrack extends gameSprite {

    // Crack states
    private float leakRateOptions[] = {0.5f, 1.0f};
    private TextureRegion fixedRegion;
    private float fixedAnimationDuration = 1.0f;
    private CrackType crackType;
    private CrackType originalCrackType; //use for monster blocking the original type
    private CrackState crackState;
    private Vector2 pipeCrackedPosition;

    // rate
    private float leakRate;
    private boolean hasMonster = false;

    //water drop
    private Vector2 waterDropPosition;


    public PipeCrack(Vector2 position, float scale, CrackType crackType) {
        super(
          "atlas/crack/crackx64.atlas",
            getCrackRegionName(crackType),
            position,
            scale
        );

        this.crackType = crackType;
        this.crackState = CrackState.ACTIVE;
        this.pipeCrackedPosition = new Vector2(position);

        this.waterDropPosition = new Vector2(position);
        this.waterDropPosition.set(this.getMidX(), position.y - 5f); //a position to align a bit down

        switch(crackType){
            case SMALL_CRACK:
                this.leakRate = leakRateOptions[0];
                this.hasMonster = false;
                break;
            case LARGE_CRACK:
                this.leakRate = leakRateOptions[1];
                this.hasMonster = false;
                break;
            case MONSTER_DAMAGE:
                // Determine if it's a monster on a small or large crack
                // For now, assume it's a monster on a large crack
                this.originalCrackType = CrackType.SMALL_CRACK;
                this.leakRate = leakRateOptions[1];
                this.hasMonster = true;
                break;
                default:
                    this.leakRate = leakRateOptions[1];
                    this.hasMonster = false;
                    break;
        }
    }

    private static String getCrackRegionName(CrackType crackType) {
        switch(crackType){
            case LARGE_CRACK:
                return "crack2";
            case MONSTER_DAMAGE:
                return "crack3"; //leave big crack and another monster on top
            case SMALL_CRACK:
            default:
                return "crack1";
        }
    }

    /**
     * Creates a monster-obstructed crack
     */
    public static PipeCrack createMonsterCrack(Vector2 position, float scale, CrackType baseType) {
        PipeCrack crack = new PipeCrack(position, scale, CrackType.MONSTER_DAMAGE);
        crack.originalCrackType = baseType;
        return crack;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

    }

    @Override
    public void draw(SpriteBatch batch) {
        if(crackState == CrackState.FIXED){


        }else{
            //only show if not fixed
            super.draw(batch);
        }
    }

    /**
     * Apply a repair tool to the crack
     *
     * @param tooltype The type of tool being used
     * @return true if the repair is successful, false otherwise
     */
    public boolean applyRepair(ToolType tooltype){
        if(!isCorrectTool(tooltype)){
            return false;
        }

        // If monster present, require water spray first
        if (hasMonster && tooltype != ToolType.WATER_SPRAY) {
            return false;
        }
        // If the crack is already fixed
        if (crackState == CrackState.FIXED) {
            return true;
        }

        // Set state to being fixed
        crackState = CrackState.BEING_FIXED;

        // If it's a water spray on a monster, remove the monster
        if (hasMonster && tooltype == ToolType.WATER_SPRAY) {
            hasMonster = false;

            crackType = originalCrackType;

            switch(crackType){
                case SMALL_CRACK:
                    this.leakRate = leakRateOptions[0];
                    break;
                case LARGE_CRACK:
                    this.leakRate = leakRateOptions[1];
                    break;
                    default:
                        this.leakRate = leakRateOptions[1];
                        break;

            }
            //still need to fix the undelying crack
            return false;
        }

        //if is used correct tool on right crack type
        //fix immediately
        crackState = CrackState.FIXED;
        return true;
    }

    private boolean isCorrectTool(ToolType tooltype) {
        //quick return no need switch check
        if(hasMonster){
            return tooltype == ToolType.WATER_SPRAY;
        }


        // Otherwise, check the crack type (or original type for monster damage)
        CrackType typeToCheck = (crackType == CrackType.MONSTER_DAMAGE) ?
            originalCrackType : crackType;

        switch(typeToCheck){
            case SMALL_CRACK:
                return tooltype == ToolType.DUCT_TAPE;
            case LARGE_CRACK:
                return tooltype == ToolType.PIPE_WRENCH;
            default:
                throw new IllegalStateException("Unknown crack type");

        }
    }

    /**
     * Helper method to convert string tool name to ToolType enum
     * Useful when integrating with existing code
     */
    public static ToolType getToolTypeFromString(String toolName) {
        if(toolName == null){
            return null;
        }

        switch(toolName.toLowerCase()){
            case "water_spray":
                return ToolType.WATER_SPRAY;
                case "pipe_wrench":
                return ToolType.PIPE_WRENCH;
                case "duct_tape":
                return ToolType.DUCT_TAPE;
                default:
                    System.out.println("Unkown type");
                    return null;
        }
    }

    public float getLeakRate() {
        if(crackState == CrackState.FIXED){
            return 0f;
        }
        return leakRate;
    }

    public Vector2 getWaterDropPosition() {
        return waterDropPosition;
    }

    /**
     *  If the crackState is fixed, remove it in the game.
     */
    public boolean isFixed(){
        return crackState == CrackState.FIXED;
    }

    public CrackType getCrackType(){
        return this.crackType;
    }

    public CrackType getOriginalCrackType(){
        return this.originalCrackType;
    }

    public boolean hasMonster(){
        return this.hasMonster;
    }
}
