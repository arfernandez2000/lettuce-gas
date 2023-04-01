import sys

import numpy as np
import pandas as pd
from ovito.data import DataCollection, SimulationCell, Particles
from ovito.io import export_file
from ovito.pipeline import StaticSource, Pipeline


def export_to_ovito(frame_file):
    data_frame = get_particle_data(frame_file)

    pipeline = Pipeline(source=StaticSource(data=DataCollection()))

    def simulation_cell(frame, data):
        cell = SimulationCell()
        cell[:, 0] = (7, 0, 0)  # va el L donde estan los 7s
        cell[:, 1] = (0, 7, 0)
        cell[:, 2] = (0, 0, 2)
        data.objects.append(cell)

        particles = get_particles(data_frame[frame])
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

def particle_list_to_color(particle_list: list) -> tuple:
    density = particle_list.count(1)
    if density == 0:
        return 0, 1.0, 1.0
    if density == 1:
        return 0, 1.0, 0
    if density == 2:
        return 1.0, 1.0, 1.0
    if density == 3:
        return 1.0, 0.2, 0.9
    if density == 4:
        return 0.6, 0.9, 0.1
    if density == 5:
        return 1.0, 0.2, 0.9
    if density == 6:
        return 1.0, 1.0, 0.3

color_rgb_list = []


def get_particle_data(frame_file):
    frames = []
    with open(frame_file, "r") as frame:
        next(frame)
        next(frame)
        next(frame)
        next(frame)
        frame_lines = []
        for line in frame:
            ll = line.split('\t')
            line_info = []
            if len(ll) > 1:
                # Parses the particle list of bits and converts it to a rgb value. Example, [1,0,1,0,0,1] -> (1.0, 1.0, 0,5)
                color_rgb = particle_list_to_color(ll[3].replace("\n","").replace("[","").replace("]","").split(","))
                color_rgb_list.append(color_rgb)
            for index in ll:
                if index.isalpha():
                    index = direction_to_angle(index)
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


def get_particles(data_frame):
    particles = Particles()
    # particles.create_property('Particle Identifier', data=data_frame.id)
    particles.create_property("Position", data=np.array((data_frame.x, data_frame.y, data_frame.z)).T)
    # particles.create_property('Radius', data=data_frame.radius)
    # particles.create_property('Color', data=np.array(color_rgb_list))
    # particles.create_property('Color', data=np.array(color_rgb_list[-5000:]))
    particles.create_property('Color', data=np.array(color_rgb_list))
    # particles.create_property('Color', data=(1.0, 1.0, 0))
    # particles.create_property('Angle', data=data_frame.angle)
    # particles.create_property("Force", data=np.array(
    #     (np.cos(data_frame.angle) * data_frame.speed, np.sin(data_frame.angle) * data_frame.speed, data_frame.z)).T)

    return particles


if __name__ == '__main__':
    export_to_ovito(sys.argv[1])
