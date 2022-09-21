// TODO: Implement Later

// package sab.game.stages;

// import com.badlogic.gdx.math.Vector2;

// import sab.game.Battle;

// public class CustomStageReader {
//     public StageObject texture(String image, float x, float y, float width, float height, Stage stage) {
//         return new StageObject(x, y, width, height, image, stage);
//     }

//     public StageObject platform(String image, float x, float y, float width, float height, Stage stage) {
//         return new Platform(x, y, width, height, image, stage);
//     }

//     public StageObject movingPlatform(String image, float x, float y, float width, float height, Stage stage, Vector2 target, float moveSpeed, boolean smoothMovement) {
//         return new Platform(x, y, width, height, image, stage, new StageObjectBehaviour() {

//             private Vector2 target1 = target;
//             private Vector2 target2 = new Vector2(x, y);
//             private float speed = moveSpeed;

//             @Override
//             public void update(StageObject stageObject, Battle battle) {
//             }
//         });
//     }
// }
