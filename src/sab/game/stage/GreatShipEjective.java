package sab.game.stage;

import com.badlogic.gdx.math.Vector2;
import sab.game.ai.pathfinding.Graph;
import sab.game.ai.pathfinding.Node;
import sab.game.ai.pathfinding.NodeType;

public class GreatShipEjective extends StageType {
    // width, height, x, 450 * 4 - y
    private static final int[] PLATFORMS = new int[] {
            68, 12, 184, 108,
            24, 12, 284, 108,
            22, 22, 168, 220,
            26, 10, 400, 250,
            48, 20, 510, 186,
            34, 24, 590, 210,
            20, 30, 184, 328,
            16, 22, 120, 220,
            18, 12, 482, 338,
            40, 12, 410, 338,
            36, 20, 100, 142,
            16, 26, 120, 162,
            38, 24, 98, 242,
            16, 48, 236, 296,
            34, 26, 590, 152,
            6, 24, 494, 350,
            29, 22, 400, 194,
            14, 20, 554, 132,
            25, 16, 270, 328,
            30, 3, 470, 110,
            22, 25, 168, 163,
            21, 66, 349, 194,
            84, 24, 168, 242,
            68, 30, 184, 266,
            24, 7, 228, 235,
            96, 19, 228, 216,
            10, 37, 228, 163,
            54, 22, 184, 120,
            18, 8, 100, 134,
            70, 8, 168, 142,
            20, 4, 98, 266,
            10, 42, 556, 240,
            14, 2, 510, 206,
            12, 4, 510, 148,
            3, 16, 519, 132,
            29, 35, 470, 113,
            10, 53, 410, 350,
            16, 34, 410, 260,
            62, 450, 0, 0,
            684, 2, 0, 0,
            386, 34, 0, 416,
            274, 34, 410, 416,
            6, 450, 678, 0,
            88, 182, 590, 234,
            24, 175, 566, 240,
            25, 66, 324, 194,
            101, 65, 103, 358,
            43, 73, 75, 270,
            122, 13, 204, 376,
            53, 50, 132, 4,
            124, 47, 184, 29,
            37, 102, 81, 32,
            30, 59, 470, 15,
            33, 23, 500, 15,
            79, 136, 568, 16,
            95, 8, 429, 208,
            26, 60, 498, 216,
            22, 36, 304, 283,
            10, 33, 298, 120,
            55, 29, 494, 374,

            10, 16, 228, 200,
            19, 4, 324, 190,
            10, 12, 298, 153,
            7, 32, 449, 176,
            26, 6, 498, 276,
            90, 12, 410, 294,
            10, 13, 410, 403,
            22, 25, 304, 319,
            9, 16, 295, 328,
            22, 4, 304, 279,
            22, 13, 168, 150,
            10, 13, 228, 150,
            20, 16, 499, 132,
            8, 7, 470, 148
    };

    private static final int[] SLOPES = new int[] {
            348, 194, 307, 152, 1,
            470, 46, 425, 1, -1,
            532, 36, 568, 72, -1,
            499, 112, 519, 132, 1,
            646, 151, 678,  183, -1,
            678, 205, 649, 234, -1,
            566, 338, 549, 373, -1,
            494, 402, 480, 416, -1,
            433, 416, 419, 402, 1,
            353, 416, 325, 388, 1,
            133, 358, 117, 342, 1,
            75, 270, 61, 256, 1,
            61, 153, 82, 133, 1,
            117, 69, 133, 53, 1,
            307, 29, 335, 1, 1,
            237, 199, 245, 216, 1,
            189, 163, 203, 149, 1,
            214, 149, 228, 163, -1,
            298, 164, 324, 190, -1,
            324, 259, 304, 279, -1,
            349, 259, 325, 283, 1,
            304, 318, 294, 328, -1,
            428, 194, 470, 152, -1,
            455, 177, 477, 155, 1,
            488, 294, 498, 275, -1,
            511, 280, 499, 305, 1
    };

    @Override
    public void init(Stage stage) {
        stage.name = "Great Ship Ejective";
        stage.background = "background.png";
        stage.music = "invasion.mp3";
        stage.id = "great_ship_ejective";
        stage.safeBlastZone.setSize(684 * 4, 450 * 4);
        stage.safeBlastZone.setCenter(new Vector2(0, 0));
        stage.unsafeBlastZone.setSize(684 * 4 + 256, 450 * 4 + 256);
        stage.unsafeBlastZone.setCenter(new Vector2(0, 0));
        stage.maxZoomOut = 2f;

        // (208, 172), (492, 165)
        stage.player1SpawnX = 208 * 4 - 684 * 2;
        stage.player1SpawnY = (450 - 172) * 4 - 450 * 2;
        stage.player2SpawnX = 492 * 4 - 684 * 2;
        stage.player2SpawnY = (450 - 165) * 4 - 450 * 2;

        stage.descendingRespawnPlatforms = false;

        StageObject walls = new StageObject(-684 * 2, -450 * 2, 684 * 4, 450 * 4, "great_ship_ejective_walls.png", stage);
        stage.addStageObject(walls);
        StageObject ship = new StageObject(-684 * 2, -450 * 2, 684 * 4, 450 * 4, "great_ship_ejective.png", stage);
        stage.addStageObject(ship);

        for (int i = 0; i < PLATFORMS.length; i += 4) {
            int width = PLATFORMS[i] * 4;
            int height = PLATFORMS[i + 1] * 4;
            int x = PLATFORMS[i + 2] * 4 - 684 * 2;
            int y = (450 - PLATFORMS[i + 3]) * 4 - 450 * 2 - height;

            Platform platform = new Platform(x, y, width, height, "none.png", stage);
            stage.addStageObject(platform);
            //platform.createLedges(stage);
        }
        for (int i = 0; i < SLOPES.length; i += 5) {
            int x1 = SLOPES[i] * 4 - 684 * 2;
            int y1 = (450 - SLOPES[i + 1]) * 4 - 450 * 2;
            int x2 = SLOPES[i + 2] * 4 - 684 * 2;
            int y2 = (450 - SLOPES[i + 3]) * 4 - 450 * 2;

            stage.addSlope(new Slope(x1, y1, x2, y2, SLOPES[i + 4]));
        }

        stage.addStageObject(new Platform(64, 300, 112, 32, "sussy_table.png", stage).createLedges(stage));
        stage.addStageObject(new Platform(280f, 500, 112, 32, "sussy_table.png", stage).createLedges(stage));
        stage.addStageObject(new Platform(0, 600, 112, 32, "sussy_table.png", stage).createLedges(stage));
        stage.addStageObject(new PassablePlatform(252 * 4 - 684 * 2, (450 - 113) * 4 - 450 * 2 - 12, 128, 12, "industrial_platform.png", stage));
        stage.addStageObject(new PassablePlatform(252 * 4 - 684 * 2, (450 - 113) * 4 - 450 * 2 - 12 - 200, 128, 12, "industrial_platform.png", stage));
        stage.addStageObject(new PassablePlatform(136 * 4 - 684 * 2, (450 - 184) * 4 - 450 * 2 - 12, 128, 12, "industrial_platform.png", stage));
        stage.addStageObject(new PassablePlatform(136 * 4 - 684 * 2, (450 - 243) * 4 - 450 * 2 - 12, 128, 12, "industrial_platform.png", stage));
        stage.addStageObject(new PassablePlatform(370 * 4 - 684 * 2, (450 - 302) * 4 - 450 * 2 - 12, 128, 12, "industrial_platform.png", stage));
        stage.addStageObject(new PassablePlatform(524 * 4 - 684 * 2, (450 - 243) * 4 - 450 * 2 - 12, 128, 12, "industrial_platform.png", stage));
        stage.addStageObject(new PassablePlatform(524 * 4 - 684 * 2, (450 - 303) * 4 - 450 * 2 - 12, 128, 12, "industrial_platform.png", stage));

        // Some nodes need to be added manually
        stage.graph.addNode(new Node(new Vector2(-400, -574), NodeType.AIR));
        stage.graph.addNode(new Node(new Vector2(-345, -574), NodeType.AIR));
        stage.graph.addNode(new Node(new Vector2(494, -459), NodeType.AIR));
        stage.graph.addNode(new Node(new Vector2(494, -734), NodeType.AIR));
        stage.graph.addNode(new Node(new Vector2(726, 533), NodeType.AIR));
    }
}
