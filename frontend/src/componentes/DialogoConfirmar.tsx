import type { SetStateAction } from "react"
import type React from "react"

export type DialogoConfirmarOpts = {
    mensaje: string,
    titulo?: string,
    onConfirm: ()=>void,
    onCancel: ()=>void,
}

export function DialogoConfirmar(props: DialogoConfirmarOpts) {
    const { mensaje, titulo, onConfirm, onCancel } = props

    return <>
    <div className="fixed top-0 left-0">
        <div className="absolute bg-black opacity-15 w-screen h-screen z-20">
        </div>

        <div id="cancel-area" className="absolute flex justify-center items-center w-screen h-screen z-30" 
            onClick={(e) => {if (e.target == document.getElementById("cancel-area")) onCancel()}}
        >
            <div className="bg-white opacity-100 z-10 rounded-md shadow-md min-w-72 min-h-20">
                {titulo && <>
                    <div className="w-full bg-blue-500 mb-2 p-3 rounded-t-md">
                        <h1 className="text-3xl text-white font-semibold">{titulo}</h1>
                    </div>
                </>
                }
                <div className="p-3">
                    <p className="pb-3">{mensaje}</p>
                    <div className="flex flex-row gap-3 justify-end">
                        <button onClick={onCancel} className="group p-2 rounded-md hover:text-white hover:bg-red-600 transition duration-300">
                            <i className="fa-solid fa-xmark mr-1 text-red-600 group-hover:text-white"></i>
                            Cancelar
                        </button>
                        <button onClick={onConfirm} className="group p-2 rounded-md hover:text-white hover:bg-green-600 transition duration-300">
                            <i className="fa-solid fa-check mr-1 text-green-600 group-hover:text-white"></i>
                            Confirmar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </>

}