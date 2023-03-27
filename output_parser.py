import sys

import numpy as np
import pandas as pd

def parse_data(output_file):

    frames = []
    with open(output_file, "r") as frame:
        next(frame)
        next(frame)
        next(frame)
        next(frame)
        frame_lines = []
        for line in range(len(frame)):
            ll = frame(line).split()
            line_content = calculate_info(line, ll)

            if len(line_content) > 1:
                frame_lines.append(line_content)
            elif len(line_content) == 0:
                df = pd.DataFrame(np.array(frame_lines), columns=["x", "y", "angles"])
                frames.append(df)
                frame_lines = []
        df = pd.DataFrame(np.array(frame_lines), columns=["x", "y", "anlges"])
        frames.append(df)
    print(frames)

    return frames
# 0
# 3	1	0	0	0	1	0	0	0	0	1
def calculate_info(line, ll):
    line_content = []
    if(len(ll) > 1):
        line_content.append(float(ll[0])) #x
        if line % 2 != 0 and index == 0:
            line_content.append(float(ll[1] + 0.5)) #y
        else:
            line_content.append(float(ll[1])) #y

        for index in range(2, len(ll)):
            angles = []
            if ll[index] == 1:
                angles.append((index - 1) * 60)
        line_content.append(angles)


if __name__ == '__main__':
    parse_data(sys.argv[1])