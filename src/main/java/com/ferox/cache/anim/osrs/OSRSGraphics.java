package com.ferox.cache.anim.osrs;

import com.ferox.cache.anim.Sequence;
import com.ferox.cache.anim.SpotAnimation;

import static com.ferox.cache.anim.SpotAnimation.cache;

/**
 * @author Patrick van Elderen | May, 27, 2021, 21:58
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class OSRSGraphics {

    public static void unpack(int graphic) {

        if(graphic == 5000) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].animation_id = 6960;
            cache[graphic].seq = Sequence.cache[6960];
            cache[graphic].model_id = 58928;
            cache[graphic].model_scale_x = 180;
            cache[graphic].model_scale_y = 180;
        }

        if (graphic == 5001) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].animation_id = 465;
            cache[graphic].seq = Sequence.cache[465];
            cache[graphic].model_id = 58925;
            cache[graphic].model_scale_x = 80;
            cache[graphic].model_scale_y = 80;
        }

        if(graphic == 5002) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].animation_id = 7080;
            cache[graphic].seq = Sequence.cache[7080];
            cache[graphic].model_id = 58926;
        }

        if (graphic == 5004) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].animation_id = 8287;
            cache[graphic].seq = Sequence.cache[8287];
            cache[graphic].model_id = 58929;
        }

        if (graphic == 5005) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].animation_id = 5358;
            cache[graphic].seq = Sequence.cache[5358];
            cache[graphic].model_id = 58930;
        }

        if (graphic == 10006) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].model_id = 3479;
            cache[graphic].animation_id = 1061;
            cache[graphic].seq = Sequence.cache[1061];
            cache[graphic].ambient = 50;
            cache[graphic].contrast = 50;
            cache[graphic].src_color = new int[]{960};
            cache[graphic].dst_color = new int[]{-12535};
        }

        if (graphic == 10004) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].model_id = 42604;
            cache[graphic].ambient = 40;
            cache[graphic].contrast = 120;
            cache[graphic].src_color = new int[]{-32539, 127};
            cache[graphic].dst_color = new int[]{-32535, -32541};
        }

        if (graphic == 10005) {
            cache[graphic] = new SpotAnimation();
            cache[graphic].id = graphic;
            cache[graphic].model_id = 42603;
            cache[graphic].animation_id = 366;
            cache[graphic].seq = Sequence.cache[366];
            cache[graphic].ambient = 40;
            cache[graphic].contrast = 120;
            cache[graphic].src_color = new int[]{-32539, 127};
            cache[graphic].dst_color = new int[]{-32535, -32541};
        }
    }
}
