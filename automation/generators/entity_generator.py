from pathlib import Path


class EntityGenerator:

    def __init__(self, project_root):
        self.project_root = Path(project_root)

    def create(self, service_name: str, entity_name: str):

        base = (
            self.project_root /
            "backend" /
            f"{service_name}-service" /
            "src" /
            "main" /
            "java" /
            "br" /
            "com" /
            "spp" /
            service_name /
            "entity"
        )

        base.mkdir(parents=True, exist_ok=True)

        entity_file = base / f"{entity_name}.java"

        if entity_file.exists():
            print(f"{entity_name}.java já existe.")
            return

        code = f"""package br.com.spp.{service_name}.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="{entity_name.lower()}s")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class {entity_name} {{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

}}
"""

        entity_file.write_text(code, encoding="utf-8")

        print(f"Entity criada em:")
        print(entity_file)