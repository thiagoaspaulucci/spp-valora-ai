from pathlib import Path
import click

from generators.entity_generator import EntityGenerator


ROOT = Path(__file__).resolve().parent.parent


@click.group()
def cli():
    pass


@cli.command()
@click.argument("service")
@click.argument("entity")
def entity(service, entity):
    """
    Cria uma Entity
    """

    EntityGenerator(ROOT).create(service, entity)


if __name__ == "__main__":
    cli()