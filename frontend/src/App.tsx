import { Routes, Route, Navigate } from "react-router-dom";
import BarraSup from "./componentes/BarraSuperior";
import ListaEventos from "./pantallas/eventos/ListaEventos";
import Login from "./pantallas/Login";
import Registro from "./pantallas/Registro";

import { useLocation } from "react-router-dom";

function App() {
  const location = useLocation();
  const hideBar =
    location.pathname === "/login" || location.pathname === "/registro";
  return (
    <>
      {!hideBar && <BarraSup />}
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />
        <Route path="/eventos" element={<ListaEventos />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </>
  );
}

export default App;
