/*
 * Copyright (c) 2019 Andrii Chubko
 */

package ui.util;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({SeekBarButtonType.INCREMENT, SeekBarButtonType.DECREMENT })
public @interface SeekBarButtonType {
  int INCREMENT = 0;
  int DECREMENT = 1;
}
