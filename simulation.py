# Example file showing a basic pygame "game loop"
import pygame
from output_parser import parse_data
from polygonHelper import OFFSET_Y, draw_hexagon_row, draw_main_hexagons, draw_middle_wall, draw_regular_polygon, draw_solid_hexagon

frames = []
borders = []
parse_data("src/main/resources/output.txt", frames, borders)


HEX_RADIUS = 50
X_START_EVEN = HEX_RADIUS
X_START_ODD = HEX_RADIUS + HEX_RADIUS
Y_START = HEX_RADIUS    

# pygame setup
pygame.init()
screen = pygame.display.set_mode((1280, 1000))
clock = pygame.time.Clock()
running = True

while running:
    # poll for events
    # pygame.QUIT event means the user clicked X to close your window
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False

    # fill the screen with a color to wipe away anything from last frame
    screen.fill("white")

    # def draw_main_hexagons(surface, color, radius, initial_position, cols, rows):
    draw_main_hexagons(screen, pygame.Color(0,0,0), HEX_RADIUS, (X_START_EVEN, Y_START), 200, 200)
    draw_middle_wall(screen, pygame.Color(255,255,0), HEX_RADIUS, 10, 200)

    draw_solid_hexagon(screen, pygame.Color(255,255,0), HEX_RADIUS, 2,4)

    # draw_hexagon_row(screen, pygame.Color(100,100,10), HEX_RADIUS, (X_START_EVEN, Y_START + 0 * HEX_RADIUS*2), 10)
    # draw_hexagon_row(screen, pygame.Color(100,100,10), HEX_RADIUS, (X_START_ODD, Y_START + 1 * HEX_RADIUS*2), 10)
    # draw_hexagon_row(screen, pygame.Color(100,100,10), HEX_RADIUS, (X_START_EVEN, Y_START + 2 * HEX_RADIUS*2), 10)
    # draw_hexagon_row(screen, pygame.Color(100,100,10), HEX_RADIUS, (X_START_ODD, Y_START + 3 * HEX_RADIUS*2), 10)
    # (surface, color, radius, initial_position, hexagon_amount)

    # flip() the display to put your work on screen
    pygame.display.flip()

    clock.tick(60)  # limits FPS to 60

pygame.quit()

