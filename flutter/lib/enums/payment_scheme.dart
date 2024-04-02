
enum PaymentScheme{

  MADA(name: "mada"),
  MADA_VISA(name: "mada_visa"),
  MADA_MASTERCARD(name: "mada_mastercard"),
  VISA(name: "visa"),
  MASTERCARD(name: "mastercard"),
  MAESTRO(name: "maestro"),
  UNION_PAY(name: "unionpay"),
  PAY_PAK(name: "paypak"),
  AMEX(name: "amex"),
  DISCOVER(name: "discover"),
  MEEZA(name: "meeza");


  final String name;
  const PaymentScheme({required this.name});
}
