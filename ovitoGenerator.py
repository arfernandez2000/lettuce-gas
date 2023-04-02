import sys

import numpy as np
import pandas as pd
from ovito.data import DataCollection, SimulationCell, Particles
from ovito.io import export_file
from ovito.pipeline import StaticSource, Pipeline
from functools import reduce


def export_to_ovito(frame_file):
    color_dictionary = {}
    data_frame = get_particle_data(frame_file, color_dictionary)

    pipeline = Pipeline(source=StaticSource(data=DataCollection()))

    def simulation_cell(frame, data):
        cell = SimulationCell()
        cell[:, 0] = (7, 0, 0)  # va el L donde estan los 7s
        cell[:, 1] = (0, 7, 0)
        cell[:, 2] = (0, 0, 2)
        data.objects.append(cell)

        particles = get_particles(data_frame[frame], color_dictionary)
        data.objects.append(particles)

    pipeline.modifiers.append(simulation_cell)

    export_file(pipeline, 'results_ovito1.1.dump', 'lammps/dump',
                columns=["Position.X", "Position.Y", "Position.Z", "Color.R", "Color.G", "Color.B"],
                multiple_frames=True, start_frame=0, end_frame=len(data_frame) - 1)


def direction_to_angle(direction: str) -> int:
    base_angle = 60
    if direction == "A":
        return base_angle
    elif direction == "B":
        return 0
    elif direction == "C":
        return 5 * base_angle
    elif direction == "D":
        return 4 * base_angle
    elif direction == "E":
        return 3 * base_angle
    elif direction == "F":
        return 2 * base_angle
    print("error de angulo")
    return 0


def density_to_color(density: float) -> tuple:
    if density == 1.0:
        return 0.77, 0.83, 0.97 # 7
    if density > 1.6:
        return 0.0, 0.04, 0.54 # 0
    if density > 1.5:
        return 0.11, 0.17, 0.63 # 1
    if density > 1.4:
        return 0.22, 0.31, 0.72  # 2
    if density > 1.3:
        return 0.33, 0.44, 0.82 # 3
    if density > 1.2:
        return 0.43, 0.57, 0.9 # 4
    if density > 1.1:
        return 0.55, 0.70, 1.0 # 5
    if density > 1.0:
        return 0.8, 0.87, 1.0 # 6


def particle_list_to_density(particle_list: list) -> int:
    return [int(x) for x in particle_list].count(1)


def calculate_densities(elements: dict, N: int, poor_victim: str, iteration: int) -> str:
    # N being the max index of squares
    poor_victim += str(iteration) + '\n'
    for x in elements:
        if elements.get(x) is None:
            break
        for y in elements.get(x):
            if elements.get(x).get(y) is None:
                break
            value = reduce(lambda a, b: a + b, elements.get(x).get(y)) / len(elements.get(x).get(y))
            elements[x][y] = value
            poor_victim += str(x) + '\t' + str(y) + '\t' + str(0) + '\t' + str(value) + '\n'

    return poor_victim


color_rgb_list = []


def reduce_dimention(frame) -> str:
    # don't try this at home
    global elements
    n = 5000
    l = 200
    poor_victim = ""
    square_side = 5
    file = frame.readlines()
    line_count = 0
    iteration = 0
    while line_count < len(file):
        elements = {}
        for i in range(n):
            if line_count == len(file):
                break
            line = file[line_count].split('\t')
            if line.__len__() == 1:
                line_count += 1
                break
            x = int(line[0]) // square_side
            y = int(line[1]) // square_side
            if elements.get(x) is None:
                elements[x] = {}
            if elements[x].get(y) is None:
                elements[x][y] = []
            elements[x][y].append(
                particle_list_to_density(line[2].replace("\n", "").replace("[", "").replace("]", "").split(",")))
            line_count += 1
        poor_victim = calculate_densities(elements, l / square_side, poor_victim, iteration)
        iteration += 1
        # concat_to_poor_victim(poor_victim, elements)
    return poor_victim


def get_particle_data(frame_file, color_dictionary: dict):
    frames = []
    with open(frame_file, "r") as frame:
        next(frame)
        next(frame)
        next(frame)
        next(frame)

        poor_victim = reduce_dimention(frame)
        poor_victim = poor_victim[2:]
        pls_stop = poor_victim.split('\n')
        frame_lines = []
        line_count = 0
        iteration = 0
        for line in pls_stop:
            ll = line.split('\t')
            line_info = []
            if len(ll) > 1:
                # Parses the particle list of bits and converts it to a rgb value. Example, [1,0,1,0,0,1] -> (1.0, 1.0, 0,5)
                color_rgb = density_to_color(float(ll[3]))
                color_rgb_list.append(color_rgb)
                if color_dictionary.get(iteration) is None:
                    color_dictionary[iteration] = []
                color_dictionary[iteration].append(color_rgb)
                # line_count += 1
            if len(ll) == 1:
                # print(line_count)
                iteration += 1
            for index in ll:
                if index.isnumeric():
                    line_info.append(float(index))
            if len(line_info) > 1:
                frame_lines.append(line_info)
            elif len(line_info) == 1:
                # df = pd.DataFrame(np.array(frame_lines), columns=["id", "x", "y", "z", "speed", "angle", "radius"])
                # df = pd.DataFrame(np.array(frame_lines), columns=["x", "y", "z", "color"])
                df = pd.DataFrame(np.array(frame_lines), columns=["x", "y", "z"])
                frames.append(df)
                frame_lines = []
        # df = pd.DataFrame(np.array(frame_lines), columns=["id", "x", "y", "z", "speed", "angle", "radius"])
        # df = pd.DataFrame(np.array(frame_lines), columns=["x", "y", "z", "color"])
        df = pd.DataFrame(np.array(frame_lines), columns=["x", "y", "z"])
        frames.append(df)
    print(frames)

    return frames


color_offset = 0


def get_particles(data_frame, color_dictionary: dict):
    global color_offset
    particles = Particles()
    # particles.create_property('Particle Identifier', data=data_frame.id)
    particles.create_property("Position", data=np.array((data_frame.x, data_frame.y, data_frame.z)).T)
    # particles.create_property('Radius', data=data_frame.radius)
    # particles.create_property('Color', data=np.array(color_rgb_list))
    particles.create_property('Color', data=np.array(color_dictionary[color_offset]))
    # particles.create_property('Color', data=np.array(color_rgb_list[-(color_dictionary[color_offset]):]))
    color_offset += 1
    # particles.create_property('Color', data=np.array(color_rgb_list))
    # particles.create_property('Color', data=(1.0, 1.0, 0))
    # particles.create_property('Angle', data=data_frame.angle)
    # particles.create_property("Force", data=np.array(
    #     (np.cos(data_frame.angle) * data_frame.speed, np.sin(data_frame.angle) * data_frame.speed, data_frame.z)).T)

    return particles


if __name__ == '__main__':
    export_to_ovito(sys.argv[1])
