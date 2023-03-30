import sys

import numpy as np
import pandas as pd


def parse_data(output_file, frames, borders):
    with open(output_file, "r") as frame:
        next(frame)
        next(frame)
        next(frame)
        next(frame)
        frame_lines = []
        i = 0
        for line in frame:
            ll = line.split()
            line_content = calculate_info(i, ll, borders)
            if len(line_content) > 1:
                frame_lines.append(line_content)
            elif len(line_content) == 0:
                df = pd.DataFrame(np.array(frame_lines, dtype=object), columns=["x", "y", "angles"])
                frames.append(df)
                frame_lines = []
                i = 0
            i += 1
        df = pd.DataFrame(np.array(frame_lines, dtype=object), columns=["x", "y", "angles"])
        frames.append(df)


# 0
# 3	1	0	0	0	1	0	0	0	0	1
def calculate_info(i, ll, borders):
    line_content = []
    if len(ll) > 1:
        x = 0
        y = 0
        if i % 2 != 0:
            x = float(ll[0]) + 0.5
        else:
            x = float(ll[0])
        line_content.append(x)

        y = float(ll[1])
        line_content.append(y)

        angles = []
        for index in range(2, len(ll) - 3):
            if ll[index] == "1":
                angles.append((index - 1) * 60)
        line_content.append(angles)

        if ll[len(ll) - 1] == "1":
            borders.append([x, y])

    return line_content


if __name__ == '__main__':
    parse_data(sys.argv[1], [], [])
