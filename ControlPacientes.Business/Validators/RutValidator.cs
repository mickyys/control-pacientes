using System;
using System.Text.RegularExpressions;

namespace ControlPacientes.Business.Validators;

/// <summary>
/// Validador de RUT chileno
/// </summary>
public static class RutValidator
{
    /// <summary>
    /// Valida un RUT en formato XX.XXX.XXX-X o XXXXXXXX-X
    /// </summary>
    public static bool IsValidRut(string rut)
    {
        if (string.IsNullOrWhiteSpace(rut))
            return false;

        // Remover espacios y convertir a mayúsculas
        rut = rut.Replace(".", "").Replace("-", "").Trim().ToUpper();

        if (rut.Length < 8)
            return false;

        // Validar que sea numérico excepto el último carácter
        if (!Regex.IsMatch(rut.Substring(0, rut.Length - 1), @"^\d+$"))
            return false;

        // El último carácter debe ser numérico o 'K'
        char lastChar = rut[^1];
        if (!char.IsDigit(lastChar) && lastChar != 'K')
            return false;

        // Validar el dígito verificador
        int number = int.Parse(rut.Substring(0, rut.Length - 1));
        return ValidateVerificationDigit(number, lastChar.ToString());
    }

    private static bool ValidateVerificationDigit(int number, string digit)
    {
        int sum = 0;
        int multiplier = 2;

        while (number > 0)
        {
            sum += (number % 10) * multiplier;
            multiplier++;
            if (multiplier > 7)
                multiplier = 2;
            number /= 10;
        }

        int remainder = 11 - (sum % 11);
        string expectedDigit = remainder == 11 ? "0" : remainder == 10 ? "K" : remainder.ToString();

        return digit == expectedDigit;
    }

    /// <summary>
    /// Formatea un RUT a formato estándar: XX.XXX.XXX-X
    /// </summary>
    public static string FormatRut(string rut)
    {
        if (string.IsNullOrWhiteSpace(rut))
            return string.Empty;

        rut = rut.Replace(".", "").Replace("-", "").Trim().ToUpper();

        if (rut.Length < 8)
            return rut;

        string number = rut.Substring(0, rut.Length - 1);
        string digit = rut[^1].ToString();

        // Agregar puntos cada 3 dígitos desde la derecha
        string formatted = "";
        int count = 0;
        for (int i = number.Length - 1; i >= 0; i--)
        {
            if (count == 3)
            {
                formatted = "." + formatted;
                count = 0;
            }
            formatted = number[i] + formatted;
            count++;
        }

        return $"{formatted}-{digit}";
    }
}
