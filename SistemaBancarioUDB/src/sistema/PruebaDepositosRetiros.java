package sistema;

import dao.DepositosRetirosDao;

public class PruebaDepositosRetiros {
    public static void main(String[] args) {

        DepositosRetirosDao dao = new DepositosRetirosDao();

        // Sustituye por un número de cuenta que exista en tu BD.
        String numeroCuenta = "7123681428";

        try {

            Cuenta_bancaria cuenta = dao.buscarCuenta(numeroCuenta);

            if (cuenta != null) {

                System.out.println("CUENTA ENCONTRADA");
                System.out.println("-------------------------");
                System.out.println("Número: " + cuenta.getNumeroCuenta());
                System.out.println("Saldo: " + cuenta.getSaldo());
                System.out.println("Tipo: " + cuenta.getTipoCuenta());
                System.out.println("Estado: " + cuenta.getEstado());
                System.out.println("Titular: "
                    + cuenta.getCliente().getNombreCompleto());

            } else {

                System.out.println(
                    "La cuenta no existe en el sistema."
                );
            }

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
