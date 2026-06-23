from db_config import get_connection


def obtener_usuarios_con_rostro():
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)

    sql = """
        SELECT id, nombre, apellido, identificacion, email, rol, telefono, numero_ficha, archivo
        FROM usuarios
        WHERE archivo IS NOT NULL
          AND archivo <> ''
    """
    cursor.execute(sql)
    usuarios = cursor.fetchall()

    cursor.close()
    conn.close()
    return usuarios


def obtener_usuario_por_identificacion(identificacion: str):
    conn = get_connection()
    cursor = conn.cursor(dictionary=True)

    sql = """
        SELECT id, nombre, apellido, identificacion, email, rol, numero_ficha, archivo
        FROM usuarios
        WHERE identificacion = %s
    """
    cursor.execute(sql, (identificacion,))
    usuario = cursor.fetchone()

    cursor.close()
    conn.close()
    return usuario


def actualizar_ruta_rostro(identificacion: str, ruta_archivo: str):
    conn = get_connection()
    cursor = conn.cursor()

    sql = """
        UPDATE usuarios
        SET archivo = %s
        WHERE identificacion = %s
    """
    cursor.execute(sql, (ruta_archivo, identificacion))
    conn.commit()

    filas = cursor.rowcount

    cursor.close()
    conn.close()
    return filas > 0